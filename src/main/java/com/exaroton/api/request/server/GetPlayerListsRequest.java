package com.exaroton.api.request.server;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GetPlayerListsRequest extends ServerRequest<List<String>> {

    public GetPlayerListsRequest(@NotNull ExarotonClient client, @NotNull Gson gson, @NotNull String id) {
        super(client, gson, id);
    }

    @Override
    protected String getEndpoint() {
        return "servers/{id}/playerlists/";
    }

    @Override
    protected TypeToken<APIResponse<List<String>>> getType() {
        return new TypeToken<APIResponse<List<String>>>(){};
    }
}
