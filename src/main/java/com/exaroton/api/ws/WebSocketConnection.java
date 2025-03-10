package com.exaroton.api.ws;

import com.exaroton.api.server.Server;
import com.exaroton.api.server.ServerStatus;
import com.exaroton.api.ws.stream.ConsoleStream;
import com.exaroton.api.ws.stream.ServerStatusStream;
import com.exaroton.api.ws.stream.Stream;
import com.exaroton.api.ws.stream.StreamType;
import com.exaroton.api.ws.subscriber.ServerStatusSubscriber;
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

    private CompletableFuture<Void> messageQueueCleared = new CompletableFuture<>();

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

        synchronized (streams) {
            Stream<?> stream = this.streams.get(type.getStreamClass());
            if (stream != null) {
                stream.stop();
                this.streams.remove(type.getStreamClass());
            }
        }
    }

    /**
     * execute a command using the console stream if it is active
     *
     * @param command minecraft command
     * @return was the command executed
     */
    public CompletableFuture<Void> executeCommand(String command) {
        return this.getOrCreateStream(ConsoleStream.class).executeCommand(command);
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

    /**
     * Is this connection ready
     * @return is the connection ready
     */
    public boolean isReady() {
        return ready;
    }

    private <T extends Stream<?>> @NotNull T getOrCreateStream(Class<T> clazz) {
        synchronized (streams) {
            @SuppressWarnings("unchecked") T stream = (T) this.streams.computeIfAbsent(clazz, this::createAndStartStream);
            return stream;
        }
    }

    private <T extends Stream<?>> @Nullable T getStream(Class<T> clazz) {
        synchronized (streams) {
            @SuppressWarnings("unchecked") T stream = (T) this.streams.get(clazz);
            return stream;
        }
    }

    private @NotNull Stream<?> createAndStartStream(Class<? extends Stream<?>> clazz) {
        var created = StreamType.get(clazz).construct(this, gson);
        created.start();
        return created;
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
     * @param clazz stream class
     * @param subscriber subscriber
     * @param <T> subscriber type
     */
    @ApiStatus.Internal
    public <T> void addStreamSubscriber(@NotNull Class<? extends Stream<T>> clazz, @NotNull T subscriber) {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(subscriber);
        getOrCreateStream(clazz).addSubscriber(subscriber);
    }

    /**
     * Remove a subscriber from a stream
     * @param clazz stream class
     * @param subscriber subscriber
     * @param <T> subscriber type
     */
    @ApiStatus.Internal
    public <T> void removeStreamSubscriber(@NotNull Class<? extends Stream<T>> clazz, @NotNull T subscriber) {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(subscriber);

        var stream = getStream(clazz);
        if (stream == null) {
            return;
        }

        stream.removeSubscriber(subscriber);
    }

    @ApiStatus.Internal
    public void unsubscribeFromEmptyStreams() {
        Collection<Stream<?>> streams;
        synchronized (this.streams) {
            streams = new ArrayList<>(this.streams.values());
        }
        for (Stream<?> stream : streams) {
            if (stream.hasNoSubscribers()) {
                this.unsubscribe(stream.getType());
            }
        }

        synchronized (this.messages) {
            if (!this.messages.isEmpty()) {
                return;
            }
        }

        synchronized (this.streams) {
            // server status stream is always active
            if (this.streams.size() > 1) {
                return;
            }
        }

        if (getOrCreateStream(ServerStatusStream.class).hasNoSubscribers()) {
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
                synchronized (this.messages) {
                    for (String x : this.messages) {
                        webSocket.sendText(x, true);
                    }
                    this.messages.clear();
                }
                this.messageQueueCleared.complete(null);
                this.messageQueueCleared = new CompletableFuture<>();
                break;

            default:
                final StreamType name = StreamType.get(message.get("stream").getAsString());
                final Stream<?> stream = getStream(name.getStreamClass());
                if (stream != null) {
                    stream.onMessage(type, message);
                }
        }

        return null;
    }

    @ApiStatus.Internal
    public void onStatusChange() {
        // start/stop streams based on status
        synchronized (streams) {
            for (Stream<?> s : streams.values()) {
                s.onStatusChange();
            }
        }
    }

    @ApiStatus.Internal
    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        logger.info("Websocket connection to {} closed: {} {}", uri, statusCode, reason);

        synchronized (streams) {
            for (Stream<?> stream : streams.values()) {
                stream.onDisconnected();
            }
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

    @Override
    @ApiStatus.Internal
    public void onError(WebSocket webSocket, Throwable error) {
        logger.error("Websocket connection to {} failed", uri, error);
    }

    /**
     * send data once connection is ready
     *
     * @param data web socket message
     */
    @ApiStatus.Internal
    public CompletableFuture<Void> sendWhenReady(String data) {
        if (this.client == null || !this.ready) {
            synchronized (this.messages) {
                this.messages.add(data);
            }
            return messageQueueCleared;
        }
        return this.client.sendText(data, true).thenAccept(x -> {});
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
}
