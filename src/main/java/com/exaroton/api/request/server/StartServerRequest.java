package com.exaroton.api.request.server;


import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.Server;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.HashMap;

public class StartServerRequest extends ServerRequest<Server> {
    private final boolean useOwnCredits;

    public StartServerRequest(
            @NotNull ExarotonClient client,
            @NotNull Gson gson,
            @NotNull String id,
            boolean useOwnCredits
    ) {
        super(client, gson, id);
        this.useOwnCredits = useOwnCredits;
    }

    @Override
    protected String getMethod() {
        return "POST";
    }

    @Override
    protected String getEndpoint() {
        return "servers/{id}/start/";
    }

    @Override
    protected Type getType() {
        return new TypeToken<APIResponse<?>>(){}.getType();
    }

    @Override
    protected Object getBody() {
        HashMap<String, Boolean> body = new HashMap<>();
        body.put("useOwnCredits", this.useOwnCredits);
        return body;
    }
}
