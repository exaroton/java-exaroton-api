package com.exaroton.api.request.server;

import com.exaroton.api.util.ParameterValidator;
import com.exaroton.api.ExarotonClient;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public abstract class ServerListRequest<Datatype> extends ServerRequest<Datatype> {
    private final String list;

    public ServerListRequest(
            @NotNull ExarotonClient client,
            @NotNull Gson gson,
            @NotNull String serverId,
            @NotNull String list
    ) {
        super(client, gson, serverId);
        this.list = ParameterValidator.requireNonEmpty(list, "list");
    }

    @Override
    protected HashMap<String, String> getData() {
        HashMap<String, String> map = super.getData();
        map.put("list", this.list);
        return map;
    }
}
