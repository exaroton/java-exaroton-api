package com.exaroton.api.request.server.access;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.request.server.ServerRequest;
import com.exaroton.api.server.access.ServerAccess;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GetAccessListRequest extends ServerRequest<List<ServerAccess>> {
    public GetAccessListRequest(@NotNull ExarotonClient client, @NotNull Gson gson, @NotNull String serverId) {
        super(client, gson, serverId);
    }

    @Override
    protected String getEndpoint() {
        return "servers/{id}/access/";
    }

    @Override
    protected TypeToken<APIResponse<List<ServerAccess>>> getType() {
        return new TypeToken<APIResponse<List<ServerAccess>>>(){};
    }
}
