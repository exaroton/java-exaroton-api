package com.exaroton.api.request.server.files;

import com.exaroton.api.APIRequest;
import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

public class FileDataRequest extends APIRequest<Object> {
    protected final String serverId;
    protected final String path;

    public FileDataRequest(ExarotonClient client, String serverId, String path) {
        super(client);
        this.serverId = serverId;
        this.path = path;
    }

    @Override
    protected String getEndpoint() {
        return "servers/{server}/files/data/{path}";
    }

    @Override
    protected Type getType() {
        return new TypeToken<APIResponse<Object>>(){}.getType();
    }

    @Override
    protected HashMap<String, String> getData() {
        HashMap<String, String> map = super.getData();
        map.put("server", this.serverId);
        map.put("path", this.path);
        return map;
    }
}
