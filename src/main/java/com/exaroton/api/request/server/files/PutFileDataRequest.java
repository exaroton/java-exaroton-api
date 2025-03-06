package com.exaroton.api.request.server.files;

import com.exaroton.api.ExarotonClient;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Supplier;

public class PutFileDataRequest extends FileDataRequest {
    protected final @NotNull Supplier<@NotNull InputStream> inputStream;

    public PutFileDataRequest(
            @NotNull ExarotonClient client,
            @NotNull Gson gson,
            @NotNull String serverId,
            @NotNull String path,
            @NotNull Supplier<@NotNull InputStream> stream
    ) {
        super(client, gson, serverId, path);
        this.inputStream = Objects.requireNonNull(stream);
    }

    public PutFileDataRequest(
            @NotNull ExarotonClient client,
            @NotNull Gson gson,
            @NotNull String serverId,
            @NotNull String path,
            String data
    ) {
        this(client, gson, serverId, path, () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    protected String getMethod() {
        return "PUT";
    }

    @Override
    protected HttpRequest.BodyPublisher getBodyPublisher(Gson gson, HttpRequest.Builder builder) {
        builder.header("Content-Type", "application/octet-stream");
        return HttpRequest.BodyPublishers.ofInputStream(inputStream);
    }
}
