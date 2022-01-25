package com.exaroton.api.ws.stream;

import com.exaroton.api.ws.WebSocketManager;
import com.exaroton.api.ws.data.StreamData;
import com.exaroton.api.ws.subscriber.Subscriber;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public abstract class Stream {

    private boolean shouldStart;

    /**
     * subscribers of this stream
     */
    public final List<Subscriber> subscribers = new ArrayList<>();

    /**
     * web socket client
     */
    private final WebSocketManager ws;

    public Stream(WebSocketManager ws) {
        this.ws = ws;
    }

    /**
     * send stream data through the websocket
     * @param type message type
     */
    public void send(String type) {
        ws.sendWhenReady((new Gson()).toJson(new StreamData<>(this.getName(), type)));
    }

    /**
     * @return stream name
     */
    protected abstract String getName();

    /**
     * send stream data through the websocket
     * @param type message type
     * @param data message data
     */
    public void send(String type, String data) {
        ws.sendWhenReady((new Gson()).toJson(new StreamData<>(this.getName(), type, data)));
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

    protected int[] getSupportedStatuses() {
        return new int[]{1,2,3,4};
    }

    protected boolean shouldBeStarted() {
        return this.shouldStart && ws.serverHasStatus(getSupportedStatuses());
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
