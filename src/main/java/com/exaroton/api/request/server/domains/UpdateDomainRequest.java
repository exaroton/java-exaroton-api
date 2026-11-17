package com.exaroton.api.request.server.domains;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.domains.Domain;
import com.exaroton.api.server.domains.DomainProtocol;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.http.HttpRequest;
import java.util.HashMap;

public class UpdateDomainRequest extends DomainRequest<Domain> {
    private final @Nullable DomainProtocol protocol;
    private final @Nullable Integer port;

    public UpdateDomainRequest(
            @NotNull ExarotonClient client,
            @NotNull Gson gson,
            @NotNull String serverId,
            @NotNull String id,
            @Nullable DomainProtocol protocol,
            @Nullable Integer port
    ) {
        super(client, gson, serverId, id);
        if (protocol == null && port == null) {
            throw new IllegalArgumentException("protocol or port must be set");
        }
        this.protocol = protocol;
        this.port = port;
    }

    @Override
    protected String getMethod() {
        return "PUT";
    }

    @Override
    protected TypeToken<APIResponse<Domain>> getType() {
        return new TypeToken<APIResponse<Domain>>(){};
    }

    @Override
    protected HttpRequest.BodyPublisher getBodyPublisher(Gson gson, HttpRequest.Builder builder) {
        HashMap<String, Object> body = new HashMap<>();
        if (protocol != null) {
            body.put("protocol", protocol);
        }
        if (port != null) {
            body.put("port", port);
        }
        return jsonBodyPublisher(gson, builder, body);
    }
}
