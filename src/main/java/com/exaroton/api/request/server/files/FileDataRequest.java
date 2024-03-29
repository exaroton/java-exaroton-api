package com.exaroton.api.request.server.files;

import com.exaroton.api.APIRequest;
import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

public class FileDataRequest extends FileRequest<Object> {
    public FileDataRequest(ExarotonClient client, String serverId, String path) {
        super(client, serverId, path);
    }

    @Override
    protected String getEndpoint() {
        return "servers/{server}/files/data/{path}";
    }

    @Override
    protected Type getType() {
        return new TypeToken<APIResponse<Object>>(){}.getType();
    }
}
