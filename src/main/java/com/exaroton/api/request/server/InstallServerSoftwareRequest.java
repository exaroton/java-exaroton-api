package com.exaroton.api.request.server;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.net.http.HttpRequest;
import java.util.HashMap;

public class InstallServerSoftwareRequest extends ServerSoftwareRequest<Void> {
    private final boolean reinstall;

    public InstallServerSoftwareRequest(
            @NotNull ExarotonClient client,
            @NotNull Gson gson,
            @NotNull String serverId,
            @NotNull String softwareId,
            boolean reinstall
    ) {
        super(client, gson, serverId, softwareId);
        this.reinstall = reinstall;
    }

    @Override
    protected String getMethod() {
        return "POST";
    }

    @Override
    protected TypeToken<APIResponse<Void>> getType() {
        return new TypeToken<APIResponse<Void>>(){};
    }

    @Override
    protected HttpRequest.BodyPublisher getBodyPublisher(Gson gson, HttpRequest.Builder builder) {
        HashMap<String, Boolean> body = new HashMap<>();
        body.put("reinstall", this.reinstall);
        return jsonBodyPublisher(gson, builder, body);
    }
}
