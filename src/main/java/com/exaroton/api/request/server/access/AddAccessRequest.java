package com.exaroton.api.request.server.access;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.request.server.ServerRequest;
import com.exaroton.api.server.access.ServerAccess;
import com.exaroton.api.util.ParameterValidator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.http.HttpRequest;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class AddAccessRequest extends ServerRequest<ServerAccess> {
    private final String username;
    private final @Nullable Collection<String> permissions;

    public AddAccessRequest(
            @NotNull ExarotonClient client,
            @NotNull Gson gson,
            @NotNull String serverId,
            @NotNull String username,
            @Nullable Collection<String> permissions
    ) {
        super(client, gson, serverId);
        this.username = ParameterValidator.requireNonEmpty(username, "username");
        this.permissions = permissions == null ? null : List.copyOf(permissions);
    }

    @Override
    protected String getEndpoint() {
        return "servers/{id}/access/";
    }

    @Override
    protected String getMethod() {
        return "POST";
    }

    @Override
    protected TypeToken<APIResponse<ServerAccess>> getType() {
        return new TypeToken<APIResponse<ServerAccess>>(){};
    }

    @Override
    protected HttpRequest.BodyPublisher getBodyPublisher(Gson gson, HttpRequest.Builder builder) {
        HashMap<String, Object> body = new HashMap<>();
        body.put("username", this.username);
        if (permissions != null) {
            body.put("permissions", permissions);
        }
        return jsonBodyPublisher(gson, builder, body);
    }
}
