package com.exaroton.api.request.server.files;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.config.ConfigOption;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class GetConfigOptionsRequest extends FileRequest<ConfigOption[]> {

    public GetConfigOptionsRequest(ExarotonClient client, String serverId, String path) {
        super(client, serverId, path);
    }


    @Override
    protected String getEndpoint() {
        return "servers/{server}/files/config/{path}";
    }

    @Override
    protected Type getType() {
        return new TypeToken<APIResponse<ConfigOption[]>>(){}.getType();
    }
}
