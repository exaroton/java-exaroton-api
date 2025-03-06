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

public class SetServerRAMRequest extends ServerRequest<ServerRAMInfo> {
    private final int ram;

    public SetServerRAMRequest(
            @NotNull ExarotonClient client,
            @NotNull Gson gson,
            @NotNull String id,
            int ram
    ) {
        super(client, gson, id);
        this.ram = ParameterValidator.requirePositive(ram, "ram");
    }

    @Override
    protected String getEndpoint() {
        return "servers/{id}/options/ram/";
    }

    @Override
    protected String getMethod() {
        return "POST";
    }

    @Override
    protected TypeToken<APIResponse<ServerRAMInfo>> getType() {
        return new TypeToken<APIResponse<ServerRAMInfo>>(){};
    }

    @Override
    protected HttpRequest.BodyPublisher getBodyPublisher(Gson gson, HttpRequest.Builder builder) {
        HashMap<String, Integer> body = new HashMap<>();
        body.put("ram", this.ram);
        return jsonBodyPublisher(gson, builder, body);
    }
}
