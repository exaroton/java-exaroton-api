package com.exaroton.api.request.server;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.net.http.HttpRequest;
import java.util.HashMap;

public class ExtendServerStopTimeRequest extends ServerRequest<Void> {
    private final int time;

    public ExtendServerStopTimeRequest(
            @NotNull ExarotonClient client,
            @NotNull Gson gson,
            @NotNull String serverId,
            int time
    ) {
        super(client, gson, serverId);
        this.time = time;
    }

    @Override
    protected String getEndpoint() {
        return "servers/{id}/extend-time";
    }

    @Override
    protected String getMethod() {
        return "POST";
    }

    @Override
    protected TypeToken<APIResponse<Void>> getType() {
        return new TypeToken<APIResponse<Void>>() {};
    }

    @Override
    protected HttpRequest.BodyPublisher getBodyPublisher(Gson gson, HttpRequest.Builder builder) {
        HashMap<String, Integer> body = new HashMap<>();
        body.put("time", this.time);
        return this.jsonBodyPublisher(gson, builder, body);
    }
}
