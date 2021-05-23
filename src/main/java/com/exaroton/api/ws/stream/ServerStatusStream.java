package com.exaroton.api.ws.stream;


import com.exaroton.api.ws.WSClient;
import com.exaroton.api.ws.subscriber.ServerStatusSubscriber;

public class ServerStatusStream extends Stream<ServerStatusSubscriber> {

    public ServerStatusStream(WSClient client) {
        super(client);
    }

    @Override
    protected String getName() {
        return "status";
    }
}
