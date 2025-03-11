package com.exaroton.api.ws.stream;

import com.exaroton.api.server.ServerStatus;
import com.exaroton.api.ws.WebSocketConnection;
import com.exaroton.api.ws.data.StreamData;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@ApiStatus.Internal
public abstract class Stream<T> {

    /**
     * Has this stream been started?
     */
    private boolean started;

    /**
     * Should this stream be started when the server is ready?
     */
    private boolean shouldStart;

    /**
     * subscribers of this stream
     */
    private final List<T> subscribers = new ArrayList<>();

    private final List<StreamData<?>> sendWhenStarted = new ArrayList<>();
    private CompletableFuture<Void> startedFuture = new CompletableFuture<>();

    /**
     * web socket client
     */
    protected final WebSocketConnection ws;

    /**
     * Gson instance for (de-)serialization
     */
    protected final Gson gson;

    public Stream(@NotNull WebSocketConnection ws, @NotNull Gson gson) {
        this.ws = Objects.requireNonNull(ws);
        this.gson = Objects.requireNonNull(gson);
    }

    /**
     * Get the stream type
     * @return stream type
     */
    public abstract StreamType getType();

    /**
     * Add a subscriber to this stream
     * @param subscriber subscriber
     */
    public void addSubscriber(T subscriber) {
        this.subscribers.add(subscriber);
    }

    /**
     * Remove a subscriber from this stream
     * @param subscriber subscriber
     */
    public void removeSubscriber(T subscriber) {
        this.subscribers.remove(subscriber);
        ws.unsubscribeFromEmptyStreams();
    }

    /**
     * Check if this stream has subscribers
     * @return has subscribers?
     */
    public boolean hasNoSubscribers() {
        return this.subscribers.isEmpty();
    }

    /**
     * send stream data through the websocket
     *
     * @param type message type
     * @return future that completes when the message is actually sent
     */
    private CompletableFuture<Void> send(String type) {
        return send(messageData(type, null));
    }

    private CompletableFuture<Void> send(StreamData<?> data) {
        return ws.sendWhenReady(gson.toJson(data));
    }

    protected <MessageDataType> StreamData<MessageDataType> messageData(String type, MessageDataType data) {
        return new StreamData<>(this.getType().getName(), type, data);
    }

    /**
     * Handle a message of this stream
     * @param type message type
     * @param message message data
     */
    public void onMessage(String type, JsonObject message) {
        switch (type) {
            case "started":
                this.started = true;
                synchronized (this.sendWhenStarted) {
                    this.sendWhenStarted.forEach(this::send);
                    this.sendWhenStarted.clear();
                }
                this.startedFuture.complete(null);
                this.startedFuture = new CompletableFuture<>();
                break;

            case "stopped":
                this.started = false;
                break;

            default:
                this.onDataMessage(type, message);
        }
    }

    public void onDisconnected() {
        this.started = false;
    }

    public CompletableFuture<Void> autoStartStop() {
        return this.tryToStart().thenCompose(x -> this.tryToStop());
    }

    /**
     * start stream
     */
    public void start() {
        this.shouldStart = true;
        this.tryToStart();
    }

    public CompletableFuture<Void> tryToStart() {
        if (started || !ws.isReady()) {
            return CompletableFuture.completedFuture(null);
        }

        return shouldBeStarted().thenCompose(shouldBeStarted -> {
            if (shouldBeStarted) {
                return this.send("start");
            }
            return CompletableFuture.completedFuture(null);
        });
    }

    /**
     * stop stream
     */
    public void stop() {
        this.shouldStart = false;
        this.tryToStop();
    }

    public CompletableFuture<Void> tryToStop() {
        if (!this.started) {
            return CompletableFuture.completedFuture(null);
        }

        return this.shouldBeStarted().thenCompose(shouldBeStarted -> {
            if (!shouldBeStarted) {
                return this.send("stop");
            }
            return CompletableFuture.completedFuture(null);
        });
    }

    protected List<T> getSubscribers() {
        return new ArrayList<>(this.subscribers);
    }

    protected CompletableFuture<Void> sendWhenStarted(StreamData<?> message) {
        if (this.started) {
            return this.send(message);
        } else {
            synchronized (this.sendWhenStarted) {
                this.sendWhenStarted.add(message);
            }
            return startedFuture;
        }
    }

    protected abstract void onDataMessage(String type, JsonObject message);

    protected CompletableFuture<Boolean> shouldBeStarted() {
        if (!this.shouldStart) {
            return CompletableFuture.completedFuture(false);
        }

        return ws.serverHasStatus(
                ServerStatus.ONLINE,
                ServerStatus.STARTING,
                ServerStatus.STOPPING,
                ServerStatus.RESTARTING
        );
    }
}
