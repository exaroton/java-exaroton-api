package com.exaroton.api.request.server.files;

import com.exaroton.api.ExarotonClient;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class PutFileDataRequest extends FileDataRequest {
    protected final InputStream inputStream;

    public PutFileDataRequest(ExarotonClient client, String serverId, String path, InputStream stream) {
        super(client, serverId, path);
        this.inputStream = stream;
    }

    public PutFileDataRequest(ExarotonClient client, String serverId, String path, String data) {
        super(client, serverId, path);
        this.inputStream = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
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
