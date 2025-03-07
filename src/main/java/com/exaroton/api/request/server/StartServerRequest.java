package com.exaroton.api.request.server;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.Server;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.net.http.HttpRequest;
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
    protected TypeToken<APIResponse<Server>> getType() {
        return new TypeToken<APIResponse<Server>>(){};
    }

    @Override
    protected HttpRequest.BodyPublisher getBodyPublisher(Gson gson, HttpRequest.Builder builder) {
        HashMap<String, Boolean> body = new HashMap<>();
        body.put("useOwnCredits", this.useOwnCredits);
        return jsonBodyPublisher(gson, builder, body);
    }
}
