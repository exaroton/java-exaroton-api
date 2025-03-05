package com.exaroton.api.request.server.files;

import com.exaroton.api.ExarotonClient;
import com.exaroton.api.ParameterValidator;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class GetFileDataRequest extends FileDataRequest {
    protected final String responseType;
    public GetFileDataRequest(
            @NotNull ExarotonClient client,
            @NotNull Gson gson,
            @NotNull String serverId,
            @NotNull String path,
            @NotNull String responseType
    ) {
        super(client, gson, serverId, path);
        this.responseType = ParameterValidator.requireNonEmpty(responseType, "responseType");
    }

    @Override
    protected HashMap<String, String> getHeaders() {
        HashMap<String, String> map = super.getHeaders();
        map.put("Response-Type", this.responseType);
        return map;
    }
}
