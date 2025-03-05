package com.exaroton.api.request.server.files;

import com.exaroton.api.ExarotonClient;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class CreateDirectoryRequest extends FileDataRequest {

    public CreateDirectoryRequest(
            @NotNull ExarotonClient client,
            @NotNull Gson gson,
            @NotNull String serverId,
            @NotNull String path
    ) {
        super(client, gson, serverId, path);
    }

    @Override
    protected String getMethod() {
        return "PUT";
    }

    @Override
    protected HashMap<String, String> getHeaders() {
        HashMap<String, String> map = super.getHeaders();
        map.put("Content-Type", "inode/directory");
        return map;
    }
}
