package com.exaroton.api.request.server.files;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.config.ConfigOption;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class UpdateConfigOptionsRequest extends FileRequest<ConfigOption[]> {
    private final Map<String, Object> options;

    public UpdateConfigOptionsRequest(ExarotonClient client, String serverId, String path, Map<String, Object> options) {
        super(client, serverId, path);
        this.options = options;
    }

    @Override
    protected String getEndpoint() {
        return "servers/{server}/files/config/{path}";
    }

    @Override
    protected Type getType() {
        return new TypeToken<APIResponse<ConfigOption[]>>(){}.getType();
    }

    @Override
    protected String getMethod() {
        return "POST";
    }

    @Override
    protected Object getBody() {
        return client.getGson().toJson(this.options);
    }
}
