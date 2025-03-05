package com.exaroton.api.request.server;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.ServerMOTDInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

public class GetServerMOTDRequest extends ServerRequest<ServerMOTDInfo> {

    public GetServerMOTDRequest(@NotNull ExarotonClient client, @NotNull Gson gson, @NotNull String id) {
        super(client, gson, id);
    }

    @Override
    protected String getEndpoint() {
        return "servers/{id}/options/motd/";
    }

    @Override
    protected Type getType() {
        return new TypeToken<APIResponse<ServerMOTDInfo>>(){}.getType();
    }
}
