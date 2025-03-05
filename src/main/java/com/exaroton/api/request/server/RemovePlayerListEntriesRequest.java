package com.exaroton.api.request.server;

import com.exaroton.api.ExarotonClient;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RemovePlayerListEntriesRequest extends AddPlayerListEntriesRequest {

    public RemovePlayerListEntriesRequest(
            @NotNull ExarotonClient client,
            @NotNull Gson gson,
            @NotNull String id,
            @NotNull String list,
            @NotNull List<@NotNull String> entries
    ) {
        super(client, gson, id, list, entries);
    }

    @Override
    protected String getMethod() {
        return "DELETE";
    }

}
