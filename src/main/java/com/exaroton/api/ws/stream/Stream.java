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

@ApiStatus.NonExtendable
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
    protected final List<T> subscribers = new ArrayList<>();

    /**
     * web socket client
     */
    protected final WebSocketConnection ws;

    /**
     * Gson instance for (de-)serialization
     */
    protected final Gson gson;

    @ApiStatus.Internal
    public Stream(@NotNull WebSocketConnection ws, @NotNull Gson gson) {
        this.ws = Objects.requireNonNull(ws);
        this.gson = Objects.requireNonNull(gson);
    }

    /**
     * Add a subscriber to this stream
     * @param subscriber subscriber
     */
    public void addSubscriber(T subscriber) {
        this.subscribers.add(subscriber);
    }

    /**
     * send stream data through the websocket
     * @param type message type
     */
    @ApiStatus.Internal
    public void send(String type) {
        ws.sendWhenReady(gson.toJson(new StreamData<>(this.getType().getName(), type)));
    }

    /**
     * send stream data through the websocket
     * @param type message type
     * @param data message data
     */
    @ApiStatus.Internal
    public void send(String type, String data) {
        ws.sendWhenReady(gson.toJson(new StreamData<>(this.getType().getName(), type, data)));
    }

    /**
     * Handle a message of this stream
     * @param type message type
     * @param message message data
     */
    @ApiStatus.Internal
    public void onMessage(String type, JsonObject message) {
        switch (type) {
            case "started":
                this.started = true;
                break;

            case "stopped":
                this.started = false;
                break;

            default:
                this.onDataMessage(type, message);
        }
    }

    protected abstract void onDataMessage(String type, JsonObject message);

    protected abstract StreamType getType();

    @ApiStatus.Internal
    public void onDisconnected() {
        this.started = false;
    }

    @ApiStatus.Internal
    public void onStatusChange() {
        this.tryToStart().thenCompose(x -> this.tryToStop());
    }

    @ApiStatus.Internal
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

    /**
     * start stream
     */
    @ApiStatus.Internal
    public void start() {
        this.shouldStart = true;
        this.tryToStart();
    }

    @ApiStatus.Internal
    public CompletableFuture<Void> tryToStart() {
        if (started || !ws.isReady()) {
            return CompletableFuture.completedFuture(null);
        }

        return shouldBeStarted().thenAccept(shouldBeStarted -> {
            if (shouldBeStarted) {
                this.send("start");
            }
        });
    }

    /**
     * stop stream
     */
    @ApiStatus.Internal
    public void stop() {
        this.shouldStart = false;
        this.tryToStop();
    }

    @ApiStatus.Internal
    public CompletableFuture<Void> tryToStop() {
        if (!this.started) {
            return CompletableFuture.completedFuture(null);
        }

        return this.shouldBeStarted().thenAccept(shouldBeStarted -> {
            if (!shouldBeStarted) {
                this.send("stop");
            }
        });
    }
}
