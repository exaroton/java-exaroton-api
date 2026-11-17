package com.exaroton.api.request.server;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.ServerProperties;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.net.http.HttpRequest;
import java.util.Objects;

public class SetServerPropertiesRequest extends ServerRequest<ServerProperties> {
    private final Object properties;

    /**
     * @param client     exaroton client
     * @param gson       gson instance
     * @param id         server id
     * @param properties a {@link ServerProperties} object or a map of property name to value
     */
    public SetServerPropertiesRequest(
            @NotNull ExarotonClient client,
            @NotNull Gson gson,
            @NotNull String id,
            @NotNull Object properties
    ) {
        super(client, gson, id);
        this.properties = Objects.requireNonNull(properties, "properties");
    }

    @Override
    protected String getEndpoint() {
        return "servers/{id}/options/properties/";
    }

    @Override
    protected String getMethod() {
        return "POST";
    }

    @Override
    protected TypeToken<APIResponse<ServerProperties>> getType() {
        return new TypeToken<APIResponse<ServerProperties>>(){};
    }

    @Override
    protected HttpRequest.BodyPublisher getBodyPublisher(Gson gson, HttpRequest.Builder builder) {
        return jsonBodyPublisher(gson, builder, this.properties);
    }
}
