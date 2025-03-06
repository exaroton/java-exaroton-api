package com.exaroton.api.request.server.files;

import com.exaroton.api.APIRequest;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.ParameterValidator;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

public abstract class FileRequest<T> extends APIRequest<T> {
    protected final String serverId;
    protected final String path;

    public FileRequest(@NotNull ExarotonClient client, @NotNull Gson gson, @NotNull String serverId, @NotNull String path) {
        super();
        this.serverId = ParameterValidator.requireValidId(serverId);
        this.path = Objects.requireNonNull(path, "path can not be null");
    }

    @Override
    protected HashMap<String, String> getData() {
        HashMap<String, String> map = super.getData();
        map.put("server", this.serverId);
        map.put("path", this.path);
        return map;
    }
}
