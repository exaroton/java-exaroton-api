package com.exaroton.api.request.server.files;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

public class FileDataRequest extends FileRequest<Void> {
    public FileDataRequest(
            @NotNull ExarotonClient client,
            @NotNull Gson gson,
            @NotNull String serverId,
            @NotNull String path) {
        super(client, gson, serverId, path);
    }

    @Override
    protected String getEndpoint() {
        return "servers/{server}/files/data/{path}";
    }

    @Override
    protected TypeToken<APIResponse<Void>> getType() {
        return new TypeToken<APIResponse<Void>>(){};
    }
}
