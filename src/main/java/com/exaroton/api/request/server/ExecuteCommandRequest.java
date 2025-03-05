package com.exaroton.api.request.server;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.ParameterValidator;
import com.exaroton.api.server.ServerRAMInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.HashMap;

public class ExecuteCommandRequest extends ServerRequest<ServerRAMInfo> {
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
    protected Type getType() {
        return new TypeToken<APIResponse<?>>() {
        }.getType();
    }

    @Override
    protected Object getBody() {
        HashMap<String, Object> body = new HashMap<>();
        body.put("command", this.command);
        return body;
    }
}
