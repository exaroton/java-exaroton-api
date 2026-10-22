package com.exaroton.api.request.server;

import com.exaroton.api.APIRequest;
import com.exaroton.api.APIResponse;
import com.exaroton.api.server.CreateServerOptions;
import com.exaroton.api.server.Server;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.net.http.HttpRequest;
import java.util.Objects;

public class CreateServerRequest extends APIRequest<Server> {
    private final CreateServerOptions options;

    public CreateServerRequest(@NotNull CreateServerOptions options) {
        super();
        this.options = Objects.requireNonNull(options);
    }

    @Override
    protected String getEndpoint() {
        return "servers/";
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
        return jsonBodyPublisher(gson, builder, this.options);
    }
}
