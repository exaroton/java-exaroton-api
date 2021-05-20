package com.exaroton.api.ws.data;

import com.exaroton.api.server.Server;

public class ServerStatusStreamData extends StreamData<Server> {
    public ServerStatusStreamData(String stream, String type) {
        super(stream, type);
    }
}
