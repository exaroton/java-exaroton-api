package com.exaroton.api.request.server.access;

import com.exaroton.api.ExarotonClient;
import com.exaroton.api.request.server.ServerRequest;
import com.exaroton.api.util.ParameterValidator;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public abstract class AccessRequest<Datatype> extends ServerRequest<Datatype> {
    private final String accessId;

    public AccessRequest(
            @NotNull ExarotonClient client,
            @NotNull Gson gson,
            @NotNull String serverId,
            @NotNull String accessId
    ) {
        super(client, gson, serverId);
        this.accessId = ParameterValidator.requireNonEmpty(accessId, "accessId");
    }

    @Override
    protected String getEndpoint() {
        return "servers/{id}/access/{access}/";
    }

    @Override
    protected HashMap<String, String> getData() {
        HashMap<String, String> map = super.getData();
        map.put("access", this.accessId);
        return map;
    }
}
