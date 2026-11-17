package com.exaroton.api.request.server;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.Server;
import com.exaroton.api.util.ParameterValidator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.net.http.HttpRequest;
import java.util.HashMap;

public class TransferServerRequest extends ServerRequest<Server> {
    private final String region;

    public TransferServerRequest(
            @NotNull ExarotonClient client,
            @NotNull Gson gson,
            @NotNull String id,
            @NotNull String region
    ) {
        super(client, gson, id);
        this.region = ParameterValidator.requireNonEmpty(region, "region");
    }

    @Override
    protected String getEndpoint() {
        return "servers/{id}/transfer/";
    }

    @Override
    protected String getMethod() {
        return "POST";
    }

    @Override
    protected TypeToken<APIResponse<Server>> getType() {
        return new TypeToken<APIResponse<Server>>(){};
    }

    @Override
    protected HttpRequest.BodyPublisher getBodyPublisher(Gson gson, HttpRequest.Builder builder) {
        HashMap<String, String> body = new HashMap<>();
        body.put("region", this.region);
        return jsonBodyPublisher(gson, builder, body);
    }
}
