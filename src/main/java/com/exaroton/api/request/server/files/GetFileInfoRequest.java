package com.exaroton.api.request.server.files;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.request.server.ServerRequest;
import com.exaroton.api.server.ServerFile;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

public class GetFileInfoRequest extends ServerRequest<ServerFile> {
    protected final String path;

    public GetFileInfoRequest(ExarotonClient client, String serverId, String path) {
        super(client, serverId);
        this.path = path;
    }

    @Override
    protected Type getType() {
        return new TypeToken<APIResponse<ServerFile>>(){}.getType();
    }

    @Override
    protected String getEndpoint() {
        return "servers/{id}/files/info/{path}";
    }

    @Override
    protected HashMap<String, String> getData() {
        HashMap<String, String> map = super.getData();
        map.put("path", this.path);
        return map;
    }
}
