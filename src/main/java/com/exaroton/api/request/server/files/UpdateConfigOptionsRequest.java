package com.exaroton.api.request.server.files;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.ParameterValidator;
import com.exaroton.api.server.config.ConfigOption;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class UpdateConfigOptionsRequest extends FileRequest<List<ConfigOption>> {
    private final Map<String, Object> options;

    public UpdateConfigOptionsRequest(
            @NotNull ExarotonClient client,
            @NotNull Gson gson,
            @NotNull String serverId,
            @NotNull String path,
            @NotNull Map<String, Object> options
    ) {
        super(client, gson, serverId, path);
        this.options = ParameterValidator.requireNonEmpty(options, "options");
    }

    @Override
    protected String getEndpoint() {
        return "servers/{server}/files/config/{path}";
    }

    @Override
    protected TypeToken<APIResponse<List<ConfigOption>>> getType() {
        return new TypeToken<APIResponse<List<ConfigOption>>>(){};
    }

    @Override
    protected String getMethod() {
        return "POST";
    }

    @Override
    protected Object getBody() {
        return gson.toJson(this.options);
    }
}
