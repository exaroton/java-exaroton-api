package com.exaroton.api.ws;

import com.exaroton.api.server.Server;
import com.exaroton.api.server.ServerStatus;
import com.exaroton.api.ws.stream.*;
import com.exaroton.api.ws.subscriber.*;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public final class WebSocketConnection implements WebSocket.Listener, Closeable {
    /**
     * logger
     */
    private final Logger logger = LoggerFactory.getLogger("java-exaroton-api");

    @NotNull
    private final HttpClient http;
    @NotNull
    private final Gson gson;

    @NotNull
    private final URI uri;

    @Nullable
    private WebSocket client;

    private Timer reconnectTimer;

    private boolean autoReconnect = true;

    /**
     * messages to send once the connection becomes ready
     */
    private final ArrayList<String> messages = new ArrayList<>();

    /**
     * is the connection ready
     */
    private boolean ready = false;

    /**
     * active streams
     */
    private final Map<Class<? extends Stream<?>>, Stream<?>> streams = new HashMap<>();

    @NotNull
    private final String apiToken;
    /**
     * exaroton server
     */
    private final Server server;

    /**
     * To obtain a websocket connection use {@link Server#subscribe()} and {@link Server#getWebSocket()}
     *
     * @param http     http client
     * @param gson     gson instance
     * @param uri      websocket uri
     * @param apiToken exaroton api token
     * @param server   exaroton server
     */
    @ApiStatus.Internal
    public WebSocketConnection(
            @NotNull HttpClient http,
            @NotNull Gson gson,
            @NotNull URI uri,
            @NotNull String apiToken,
            @NotNull Server server
    ) {
        this.http = http;
        this.gson = Objects.requireNonNull(gson);
        this.uri = uri;
        this.apiToken = apiToken;
        this.server = Objects.requireNonNull(server);
        this.streams.put(ServerStatusStream.class, new ServerStatusStream(this, this.gson).setServer(server));

        connect();
    }

    /**
     * subscribe to a stream if it is not already active
     *
     * @param name stream name
     */
    public void subscribe(@NotNull StreamType name) {
        Objects.requireNonNull(name);

        if (streams.containsKey(name.getStreamClass())) {
            return;
        }

        Stream<?> stream = name.construct(this, gson);
        this.streams.put(name.getStreamClass(), stream);
        stream.start();
    }

    /**
     * unsubscribe from a stream
     *
     * @param name stream name
     */
    public void unsubscribe(@NotNull StreamType name) {
        Objects.requireNonNull(name);

        Stream<?> stream = this.streams.get(name.getStreamClass());
        if (stream != null) {
            stream.stop();
            this.streams.remove(name.getStreamClass());
        }
    }

    /**
     * subscribe to server status changes
     *
     * @param subscriber instance of class handling server status changes
     */
    public void addServerStatusSubscriber(ServerStatusSubscriber subscriber) {
        this.addStreamSubscriber(ServerStatusStream.class, subscriber);
    }

    /**
     * subscribe to new console lines
     *
     * @param subscriber instance of class handling new console lines
     */
    public void addConsoleSubscriber(ConsoleSubscriber subscriber) {
        this.addStreamSubscriber(ConsoleStream.class, subscriber);
    }

    /**
     * subscribe to heap data
     *
     * @param subscriber instance of class handling heap data
     */
    public void addHeapSubscriber(HeapSubscriber subscriber) {
        this.addStreamSubscriber(HeapStream.class, subscriber);
    }

    /**
     * subscribe to stats
     *
     * @param subscriber instance of class handling stats
     */
    public void addStatsSubscriber(StatsSubscriber subscriber) {
        this.addStreamSubscriber(StatsStream.class, subscriber);
    }

    /**
     * subscribe to stats
     *
     * @param subscriber instance of class handling stats
     */
    public void addTickSubscriber(TickSubscriber subscriber) {
        this.addStreamSubscriber(TickStream.class, subscriber);
    }

    /**
     * execute a command using the console stream if it is active
     *
     * @param command minecraft command
     * @return was the command executed
     */
    public boolean executeCommand(String command) {
        ConsoleStream stream = this.getStream(ConsoleStream.class);
        if (stream != null) {
            stream.executeCommand(command);
            return true;
        }

        return false;
    }

    /**
     * en-/disable auto reconnect
     *
     * @param autoReconnect new reconnect state
     */
    public void setAutoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
    }

    /**
     * @return is auto reconnect enabled
     */
    public boolean shouldAutoReconnect() {
        return autoReconnect;
    }

    /**
     * check if the server has this status
     *
     * @param status status
     * @return true if the status matches
     */
    public CompletableFuture<Boolean> serverHasStatus(ServerStatus... status) {
        return serverHasStatus(Set.of(status));
    }

    /**
     * check if the server has one of the given statuses
     *
     * @param status status
     * @return true if the status matches
     */
    public CompletableFuture<Boolean> serverHasStatus(Set<ServerStatus> status) {
        CompletableFuture<Server> future = CompletableFuture.completedFuture(server);
        if (!this.server.isFetched()) {
            try {
                future = this.server.fetch();
            } catch (IOException e) {
                logger.error("Failed to fetch server status", e);
            }
        }
        return future.thenApply(s -> s.hasStatus(status));
    }

    private <T> void addStreamSubscriber(Class<? extends Stream<T>> c, T subscriber) {
        if (!this.streams.containsKey(c)) {
            throw new IllegalStateException("There is no active stream for: " + c);
        }

        getStream(c).addSubscriber(subscriber);
    }

    private <T extends Stream<?>> T getStream(Class<T> c) {
        @SuppressWarnings("unchecked") T stream = (T) this.streams.get(c);
        return stream;
    }

    private void connect() {
        http.newWebSocketBuilder()
                .header("Authorization", "Bearer " + apiToken)
                .buildAsync(Objects.requireNonNull(uri), this)
                .thenAccept(ws -> {
                    this.logger.debug("Connected to {}", uri);
                    this.client = ws;
                });
    }

    @ApiStatus.Internal
    @Override
    public void onOpen(WebSocket webSocket) {
        webSocket.request(1);

        logger.debug("Websocket connection opened to {}", uri);
        if (this.reconnectTimer != null) {
            this.reconnectTimer.cancel();
        }
    }

    @ApiStatus.Internal
    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        webSocket.request(1);

        var message = JsonParser.parseString(data.toString())
                .getAsJsonObject();
        var type = message.get("type").getAsString();
        switch (type) {
            case "connected":
            case "keep-alive":
            case "disconnected":
                break;

            case "ready":
                ready = true;
                for (String x : this.messages) {
                    webSocket.sendText(x, true);
                }
                this.messages.clear();

            default:
                final StreamType name = StreamType.get(message.get("stream").getAsString());
                final Stream<?> stream = streams.get(name.getStreamClass());
                if (stream != null) {
                    stream.onMessage(type, message);
                }
        }

        return null;
    }

    @ApiStatus.Internal
    public void onStatusChange() {
        // start/stop streams based on status
        for (Stream<?> s : streams.values()) {
            s.onStatusChange();
        }
    }

    @ApiStatus.Internal
    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        logger.info("Websocket connection to {} closed: {} {}", uri, statusCode, reason);

        for (Stream<?> stream : streams.values()) {
            stream.onDisconnected();
        }

        if (this.shouldAutoReconnect()) {
            reconnectTimer = new Timer();
            logger.debug("Reconnecting in 5s");
            reconnectTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    connect();
                }
            }, 5000, 5000);
        }
        return null;
    }


    /**
     * send data once connection is ready
     *
     * @param data web socket message
     */
    @ApiStatus.Internal
    public void sendWhenReady(String data) {
        if (this.client == null || !this.ready) {
            this.messages.add(data);
            return;
        }
        this.client.sendText(data, true);
    }

    /**
     * close websocket connection
     */
    @Override
    public void close() {
        if (this.reconnectTimer != null) {
            this.reconnectTimer.cancel();
        }

        if (this.client != null) {
            this.client.sendClose(0, "unsubscribe");
        }
    }

    public boolean isReady() {
        return ready;
    }
}
