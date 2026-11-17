package com.exaroton.api.request.server.domains;

import com.exaroton.api.ExarotonClient;
import com.exaroton.api.request.server.ServerRequest;
import com.exaroton.api.util.ParameterValidator;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public abstract class DomainRequest<Datatype> extends ServerRequest<Datatype> {
    private final String domainId;

    public DomainRequest(
            @NotNull ExarotonClient client,
            @NotNull Gson gson,
            @NotNull String serverId,
            @NotNull String domainId
    ) {
        super(client, gson, serverId);
        this.domainId = ParameterValidator.requireNonEmpty(domainId, "domainId");
    }

    @Override
    protected String getEndpoint() {
        return "servers/{id}/domains/{domain}/";
    }

    @Override
    protected HashMap<String, String> getData() {
        HashMap<String, String> map = super.getData();
        map.put("domain", this.domainId);
        return map;
    }
}
