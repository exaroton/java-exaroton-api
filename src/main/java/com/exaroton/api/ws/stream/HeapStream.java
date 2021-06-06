package com.exaroton.api.ws.stream;

import com.exaroton.api.ws.WebSocketManager;
import com.exaroton.api.ws.subscriber.HeapSubscriber;

public class HeapStream extends Stream<HeapSubscriber> {

    public HeapStream(WebSocketManager ws) {
        super(ws);
    }

    @Override
    protected String getName() {
        return "heap";
    }
}
