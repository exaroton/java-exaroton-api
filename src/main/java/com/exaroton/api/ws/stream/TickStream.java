package com.exaroton.api.ws.stream;

import com.exaroton.api.ws.WebSocketManager;

public class TickStream extends Stream {

    public TickStream(WebSocketManager ws) {
        super(ws);
    }

    @Override
    protected String getName() {
        return "tick";
    }
}
