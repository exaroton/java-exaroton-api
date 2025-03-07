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

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.*;
import java.util.concurrent.*;

public final class WebSocketConnection implements WebSocket.Listener {
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
     * To obtain a websocket connection use {@link Server#addStatusSubscriber(ServerStatusSubscriber)} and
     * {@link Server#getWebSocket()}
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
     * @param type stream type
     */
    public void unsubscribe(@NotNull StreamType type) {
        Objects.requireNonNull(type);

        if (type == StreamType.STATUS) {
            // Status stream can't be unsubscribed
            return;
        }

        Stream<?> stream = this.streams.get(type.getStreamClass());
        if (stream != null) {
            stream.stop();
            this.streams.remove(type.getStreamClass());
        }
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

    /**
     * Wait until the server has reached a certain status. It is highly recommended to attach a timeout to the future
     * returned by this method and/or adding the crashed status to the set of statuses to prevent the future from
     * hanging indefinitely if the server fails to start/stop.
     *
     * @param statuses the statuses to wait for
     * @return a future that completes when the server has reached one of the given statuses
     */
    public Future<Server> waitForStatus(Set<ServerStatus> statuses) {
        if (server.hasStatus(statuses)) {
            return CompletableFuture.completedFuture(server);
        }

        return new WaitForStatusSubscriber(statuses, this.getStream(ServerStatusStream.class));
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

    /**
     * Add a subscriber to a stream
     * @param c stream class
     * @param subscriber subscriber
     * @param <T> subscriber type
     */
    @ApiStatus.Internal
    public <T> void addStreamSubscriber(Class<? extends Stream<T>> c, T subscriber) {
        if (!this.streams.containsKey(c)) {
            this.streams.put(c, StreamType.get(c).construct(this, gson));
        }

        getStream(c).addSubscriber(subscriber);
    }

    /**
     * Remove a subscriber from a stream
     * @param c stream class
     * @param subscriber subscriber
     * @param <T> subscriber type
     */
    @ApiStatus.Internal
    public <T> void removeStreamSubscriber(Class<? extends Stream<T>> c, T subscriber) {
        if (!this.streams.containsKey(c)) {
            return;
        }

        getStream(c).removeSubscriber(subscriber);
        unsubscribeFromEmptyStreams();
    }

    @ApiStatus.Internal
    public void unsubscribeFromEmptyStreams() {
        for (Stream<?> stream : streams.values()) {
            if (!stream.hasSubscribers()) {
                this.unsubscribe(stream.getType());
            }
        }

        // server status stream is always active
        if (streams.size() == 1 && !getStream(ServerStatusStream.class).hasSubscribers()) {
            this.server.unsubscribe();
        }
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
     * close websocket connection. This should be called automatically when there are no remaining subscribers
     */
    @ApiStatus.Internal
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
