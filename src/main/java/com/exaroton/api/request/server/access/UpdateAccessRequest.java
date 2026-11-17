package com.exaroton.api.request.server.access;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.access.ServerAccess;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.net.http.HttpRequest;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class UpdateAccessRequest extends AccessRequest<ServerAccess> {
    private final Collection<String> permissions;

    public UpdateAccessRequest(
            @NotNull ExarotonClient client,
            @NotNull Gson gson,
            @NotNull String serverId,
            @NotNull String id,
            @NotNull Collection<String> permissions
    ) {
        super(client, gson, serverId, id);
        this.permissions = List.copyOf(permissions);
    }

    @Override
    protected String getMethod() {
        return "PUT";
    }

    @Override
    protected TypeToken<APIResponse<ServerAccess>> getType() {
        return new TypeToken<APIResponse<ServerAccess>>(){};
    }

    @Override
    protected HttpRequest.BodyPublisher getBodyPublisher(Gson gson, HttpRequest.Builder builder) {
        HashMap<String, Object> body = new HashMap<>();
        body.put("permissions", this.permissions);
        return jsonBodyPublisher(gson, builder, body);
    }
}
