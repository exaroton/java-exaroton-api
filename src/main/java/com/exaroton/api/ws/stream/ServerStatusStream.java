package com.exaroton.api.ws.stream;


import com.exaroton.api.ws.WebSocketManager;
import com.exaroton.api.ws.subscriber.ServerStatusSubscriber;

public class ServerStatusStream extends Stream<ServerStatusSubscriber> {

    public ServerStatusStream(WebSocketManager ws) {
        super(ws);
    }

    @Override
    protected String getName() {
        return "status";
    }
}
