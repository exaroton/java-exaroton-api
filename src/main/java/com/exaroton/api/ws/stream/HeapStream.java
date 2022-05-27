package com.exaroton.api.ws.stream;

import com.exaroton.api.ws.WebSocketManager;

public class HeapStream extends Stream {

    public HeapStream(WebSocketManager ws) {
        super(ws);
    }

    @Override
    protected String getName() {
        return "heap";
    }
}
