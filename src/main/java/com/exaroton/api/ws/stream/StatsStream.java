package com.exaroton.api.ws.stream;

import com.exaroton.api.ws.WebSocketManager;

public class StatsStream extends Stream {

    public StatsStream(WebSocketManager ws) {
        super(ws);
    }

    @Override
    protected String getName() {
        return "stats";
    }
}
