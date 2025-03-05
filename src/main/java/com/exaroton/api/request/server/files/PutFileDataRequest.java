package com.exaroton.api.request.server.files;

import com.exaroton.api.ExarotonClient;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class PutFileDataRequest extends FileDataRequest {
    protected final InputStream inputStream;

    public PutFileDataRequest(
            @NotNull ExarotonClient client,
            @NotNull Gson gson,
            @NotNull String serverId,
            @NotNull String path,
            @NotNull InputStream stream
    ) {
        super(client, gson, serverId, path);
        this.inputStream = stream;
    }

    public PutFileDataRequest(
            @NotNull ExarotonClient client,
            @NotNull Gson gson,
            @NotNull String serverId,
            @NotNull String path,
            String data
    ) {
        this(client, gson, serverId, path, new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    protected String getMethod() {
        return "PUT";
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }
}
