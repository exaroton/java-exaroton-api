package com.exaroton.api.request.server.files;

import com.exaroton.api.APIRequest;
import com.exaroton.api.ExarotonClient;

import java.util.HashMap;

public abstract class FileRequest<T> extends APIRequest<T> {
    protected final String serverId;
    protected final String path;

    public FileRequest(ExarotonClient client, String serverId, String path) {
        super(client);
        this.serverId = serverId;
        this.path = path;
    }

    @Override
    protected HashMap<String, String> getData() {
        HashMap<String, String> map = super.getData();
        map.put("server", this.serverId);
        map.put("path", this.path);
        return map;
    }
}
