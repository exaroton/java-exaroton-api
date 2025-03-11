package com.exaroton.api.request.server;

import com.exaroton.api.APIRequest;
import com.exaroton.api.util.ParameterValidator;
import com.exaroton.api.ExarotonClient;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public abstract class ServerRequest<Datatype> extends APIRequest<Datatype> {

    private final String serverId;

    public ServerRequest(@NotNull ExarotonClient client, @NotNull Gson gson, @NotNull String serverId) {
        super();
        this.serverId = ParameterValidator.requireValidId(serverId);
    }

    @Override
    protected HashMap<String, String> getData() {
        HashMap<String, String> map = super.getData();
        map.put("id", this.serverId);
        return map;
    }
}
