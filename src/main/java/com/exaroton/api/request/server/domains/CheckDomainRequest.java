package com.exaroton.api.request.server.domains;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.domains.Domain;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

public class CheckDomainRequest extends DomainRequest<Domain> {
    public CheckDomainRequest(@NotNull ExarotonClient client, @NotNull Gson gson, @NotNull String serverId, @NotNull String id) {
        super(client, gson, serverId, id);
    }

    @Override
    protected String getEndpoint() {
        return super.getEndpoint() + "check/";
    }

    @Override
    protected String getMethod() {
        return "POST";
    }

    @Override
    protected TypeToken<APIResponse<Domain>> getType() {
        return new TypeToken<APIResponse<Domain>>(){};
    }
}
