package com.exaroton.api.request.server;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.ParameterValidator;
import com.exaroton.api.server.ServerRAMInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.net.http.HttpRequest;
import java.util.HashMap;

public class ExecuteCommandRequest extends ServerRequest<Void> {
    private final String command;

    public ExecuteCommandRequest(
            @NotNull ExarotonClient client,
            @NotNull Gson gson,
            @NotNull String id,
            @NotNull String command
    ) {
        super(client, gson, id);
        this.command = ParameterValidator.requireNonEmpty(command, "command");
    }

    @Override
    protected String getEndpoint() {
        return "servers/{id}/command/";
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
        HashMap<String, String> body = new HashMap<>();
        body.put("command", this.command);
        return this.jsonBodyPublisher(gson, builder, body);
    }
}
