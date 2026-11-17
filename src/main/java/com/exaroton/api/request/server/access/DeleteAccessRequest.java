package com.exaroton.api.request.server.access;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

public class DeleteAccessRequest extends AccessRequest<Void> {
    public DeleteAccessRequest(@NotNull ExarotonClient client, @NotNull Gson gson, @NotNull String serverId, @NotNull String id) {
        super(client, gson, serverId, id);
    }

    @Override
    protected String getMethod() {
        return "DELETE";
    }

    @Override
    protected TypeToken<APIResponse<Void>> getType() {
        return new TypeToken<APIResponse<Void>>(){};
    }
}
