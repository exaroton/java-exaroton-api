package com.exaroton.api.ws.stream;

import com.exaroton.api.server.ServerStatus;
import com.exaroton.api.ws.WebSocketManager;
import com.exaroton.api.ws.data.StreamData;
import com.exaroton.api.ws.subscriber.Subscriber;
import com.google.gson.Gson;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ApiStatus.NonExtendable
public class Stream {

    private boolean shouldStart;

    /**
     * subscribers of this stream
     */
    public final List<Subscriber> subscribers = new ArrayList<>();

    /**
     * web socket client
     */
    private final WebSocketManager ws;

    /**
     * Gson instance for (de-)serialization
     */
    private final Gson gson;

    /**
     * stream name
     */
    private final StreamName name;

    @ApiStatus.Internal
    public Stream(@NotNull WebSocketManager ws, @NotNull Gson gson, @NotNull StreamName name) {
        this.ws = Objects.requireNonNull(ws);
        this.gson = Objects.requireNonNull(gson);
        this.name = name;
    }

    /**
     * send stream data through the websocket
     * @param type message type
     */
    public void send(String type) {
        ws.sendWhenReady(gson.toJson(new StreamData<>(this.name.getValue(), type)));
    }

    /**
     * send stream data through the websocket
     * @param type message type
     * @param data message data
     */
    public void send(String type, String data) {
        ws.sendWhenReady(gson.toJson(new StreamData<>(this.name.getValue(), type, data)));
    }

    /**
     * start stream
     */
    public void start() {
        this.shouldStart = true;
        this.tryToStart();
    }

    public void tryToStart() {
        if (shouldBeStarted()) {
            this.send("start");
        }
    }

    public void onStatusChange() {
        this.tryToStart();
        this.tryToStop();
    }

    protected boolean shouldBeStarted() {
        return this.shouldStart && ws.serverHasStatus(
                ServerStatus.ONLINE,
                ServerStatus.STARTING,
                ServerStatus.STOPPING,
                ServerStatus.RESTARTING
        );
    }

    /**
     * stop stream
     */
    public void stop() {
        this.shouldStart = false;
        this.tryToStop();
    }

    public void tryToStop() {
        if (!shouldBeStarted()) {
            this.send("stop");
        }
    }
}
