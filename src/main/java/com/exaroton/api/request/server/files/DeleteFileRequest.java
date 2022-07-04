package com.exaroton.api.request.server.files;

import com.exaroton.api.ExarotonClient;

public class DeleteFileRequest extends FileDataRequest {
    public DeleteFileRequest(ExarotonClient client, String serverId, String path) {
        super(client, serverId, path);
    }

    @Override
    protected String getMethod() {
        return "DELETE";
    }
}
