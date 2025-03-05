package com.exaroton.api.request.server;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

public class GetPlayerListsRequest extends ServerRequest<String[]> {

    public GetPlayerListsRequest(@NotNull ExarotonClient client, @NotNull Gson gson, @NotNull String id) {
        super(client, gson, id);
    }

    @Override
    protected String getEndpoint() {
        return "servers/{id}/playerlists/";
    }

    @Override
    protected Type getType() {
        return new TypeToken<APIResponse<String[]>>(){}.getType();
    }
}
