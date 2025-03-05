package com.exaroton.api.request.server.files;

import com.exaroton.api.ExarotonClient;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

public class DeleteFileRequest extends FileDataRequest {
    public DeleteFileRequest(
            @NotNull ExarotonClient client,
            @NotNull Gson gson,
            @NotNull String serverId,
            @NotNull String path
    ) {
        super(client, gson, serverId, path);
    }

    @Override
    protected String getMethod() {
        return "DELETE";
    }
}
