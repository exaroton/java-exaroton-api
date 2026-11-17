package com.exaroton.api.request.server.access;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.access.ServerAccess;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

public class GetAccessRequest extends AccessRequest<ServerAccess> {
    public GetAccessRequest(@NotNull ExarotonClient client, @NotNull Gson gson, @NotNull String serverId, @NotNull String id) {
        super(client, gson, serverId, id);
    }

    @Override
    protected TypeToken<APIResponse<ServerAccess>> getType() {
        return new TypeToken<APIResponse<ServerAccess>>(){};
    }
}
