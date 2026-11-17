package com.exaroton.api.request.server.domains;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.request.server.ServerRequest;
import com.exaroton.api.server.domains.Domain;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GetDomainsRequest extends ServerRequest<List<Domain>> {
    public GetDomainsRequest(@NotNull ExarotonClient client, @NotNull Gson gson, @NotNull String serverId) {
        super(client, gson, serverId);
    }

    @Override
    protected String getEndpoint() {
        return "servers/{id}/domains/";
    }

    @Override
    protected TypeToken<APIResponse<List<Domain>>> getType() {
        return new TypeToken<APIResponse<List<Domain>>>(){};
    }
}
