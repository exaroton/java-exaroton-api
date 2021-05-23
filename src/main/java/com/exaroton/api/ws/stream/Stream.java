package com.exaroton.api.ws.stream;

import com.exaroton.api.ws.WSClient;
import com.exaroton.api.ws.data.StreamData;
import com.google.gson.Gson;

import java.util.ArrayList;

public abstract class Stream<SubscriberType> {

    /**
     * subscribers of this stream
     */
    public final ArrayList<SubscriberType> subscribers = new ArrayList<>();

    /**
     * web socket client
     */
    private final WSClient client;

    public Stream(WSClient client) {
        this.client = client;
    }

    /**
     * send stream data through the websocket
     * @param type message type
     */
    public void send(String type) {
        client.sendWhenReady((new Gson()).toJson(new StreamData<>(this.getName(), type)));
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
        client.sendWhenReady((new Gson()).toJson(new StreamData<>(this.getName(), type, data)));
    }
}
