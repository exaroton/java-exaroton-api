package com.exaroton.api.request.server;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

public class GetPlayerListEntriesRequest extends ServerListRequest<String[]> {

    public GetPlayerListEntriesRequest(
            @NotNull ExarotonClient client,
            @NotNull Gson gson,
            @NotNull String id,
            @NotNull String list
    ) {
        super(client, gson, id, list);
    }

    @Override
    protected String getEndpoint() {
        return "servers/{id}/playerlists/{list}/";
    }

    @Override
    protected Type getType() {
        return new TypeToken<APIResponse<String[]>>(){}.getType();
    }
}
