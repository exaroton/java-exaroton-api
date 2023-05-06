package com.exaroton.api.request.server;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.ServerRAMInfo;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

public class ExecuteCommandRequest extends ServerRequest<ServerRAMInfo> {
    private final String command;

    public ExecuteCommandRequest(ExarotonClient client, String id, String command) {
        super(client, id);
        if (command == null || command.length() == 0) throw new IllegalArgumentException("Invalid command");
        this.command = command;
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
        return new TypeToken<APIResponse<?>>(){}.getType();
    }

    @Override
    protected Object getBody() {
        HashMap<String, Object> body = new HashMap<>();
        body.put("command", this.command);
        return body;
    }
}
