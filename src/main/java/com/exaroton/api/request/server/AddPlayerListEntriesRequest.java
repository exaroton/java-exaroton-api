package com.exaroton.api.request.server;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.ParameterValidator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public class AddPlayerListEntriesRequest extends ServerListRequest<List<String>> {

    private final List<String> entries;

    public AddPlayerListEntriesRequest(
            @NotNull ExarotonClient client,
            @NotNull Gson gson,
            @NotNull String id,
            @NotNull String list,
            @NotNull List<@NotNull String> entries
    ) {
        super(client, gson, id, list);
        this.entries = ParameterValidator.requireNonEmpty(entries, "entries");
    }

    @Override
    protected String getEndpoint() {
        return "servers/{id}/playerlists/{list}/";
    }

    @Override
    protected TypeToken<APIResponse<List<String>>> getType() {
        return new TypeToken<APIResponse<List<String>>>(){};
    }

    @Override
    protected String getMethod() {
        return "PUT";
    }

    @Override
    protected Object getBody() {
        HashMap<String, Object> body = new HashMap<>();
        body.put("entries", this.entries);
        return body;
    }
}
