package com.exaroton.api.request.server;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.ServerLog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

public class GetServerLogsRequest extends ServerRequest<ServerLog> {

    public GetServerLogsRequest(@NotNull ExarotonClient client, @NotNull Gson gson, @NotNull String id) {
        super(client, gson, id);
    }

    @Override
    protected String getEndpoint() {
        return "servers/{id}/logs/";
    }

    @Override
    protected TypeToken<APIResponse<ServerLog>> getType() {
        return new TypeToken<APIResponse<ServerLog>>(){};
    }
}
