package com.exaroton.api.ws.stream;


import com.exaroton.api.ws.WebSocketManager;

public class ServerStatusStream extends Stream {

    public ServerStatusStream(WebSocketManager ws) {
        super(ws);
    }

    @Override
    protected String getName() {
        return "status";
    }
}
