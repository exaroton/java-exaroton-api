package com.exaroton.api.request.server.files;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.ServerFile;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

public class GetFileInfoRequest extends FileRequest<ServerFile> {
    public GetFileInfoRequest(
            @NotNull ExarotonClient client,
            @NotNull Gson gson,
            @NotNull String serverId,
            @NotNull String path
    ) {
        super(client, gson, serverId, path);
    }

    @Override
    protected Type getType() {
        return new TypeToken<APIResponse<ServerFile>>(){}.getType();
    }

    @Override
    protected String getEndpoint() {
        return "servers/{server}/files/info/{path}";
    }
}
