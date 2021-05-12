package com.exaroton.api.request.server;

import com.exaroton.api.ExarotonClient;

import java.util.HashMap;

public abstract class ServerListRequest<Datatype> extends ServerRequest<Datatype> {
    private final String list;

    public ServerListRequest(ExarotonClient client, String serverId, String list) {
        super(client, serverId);
        if (list == null || list.length() == 0) throw new IllegalArgumentException("Invalid list name!");
        this.list = list;
    }

    @Override
    protected HashMap<String, String> getData() {
        HashMap<String, String> map = super.getData();
        map.put("list", this.list);
        return map;
    }
}
