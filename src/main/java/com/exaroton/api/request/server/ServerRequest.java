package com.exaroton.api.request.server;

import com.exaroton.api.APIRequest;
import com.exaroton.api.ExarotonClient;

import java.util.HashMap;

public abstract class ServerRequest<Datatype> extends APIRequest<Datatype> {

    private final String serverId;

    public ServerRequest(ExarotonClient client, String serverId) {
        super(client);
        if (serverId == null || serverId.length() == 0) throw new IllegalArgumentException("Invalid server id!");
        this.serverId = serverId;
    }

    @Override
    protected HashMap<String, String> getData() {
        HashMap<String, String> map = super.getData();
        map.put("id", this.serverId);
        return map;
    }
}
