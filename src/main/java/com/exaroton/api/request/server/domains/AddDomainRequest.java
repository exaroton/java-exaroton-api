package com.exaroton.api.request.server.domains;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.request.server.ServerRequest;
import com.exaroton.api.server.domains.Domain;
import com.exaroton.api.util.ParameterValidator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.net.http.HttpRequest;
import java.util.HashMap;

public class AddDomainRequest extends ServerRequest<Domain> {
    private final String domain;

    public AddDomainRequest(
            @NotNull ExarotonClient client,
            @NotNull Gson gson,
            @NotNull String serverId,
            @NotNull String domain
    ) {
        super(client, gson, serverId);
        this.domain = ParameterValidator.requireNonEmpty(domain, "domain");
    }

    @Override
    protected String getEndpoint() {
        return "servers/{id}/domains/";
    }

    @Override
    protected String getMethod() {
        return "POST";
    }

    @Override
    protected TypeToken<APIResponse<Domain>> getType() {
        return new TypeToken<APIResponse<Domain>>(){};
    }

    @Override
    protected HttpRequest.BodyPublisher getBodyPublisher(Gson gson, HttpRequest.Builder builder) {
        HashMap<String, String> body = new HashMap<>();
        body.put("domain", this.domain);
        return jsonBodyPublisher(gson, builder, body);
    }
}
