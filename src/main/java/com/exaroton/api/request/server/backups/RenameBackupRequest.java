package com.exaroton.api.request.server.backups;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.backups.Backup;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.Objects;

public class RenameBackupRequest extends BackupRequest<Backup> {
    private final String name;

    public RenameBackupRequest(
            @NotNull ExarotonClient client,
            @NotNull Gson gson,
            @NotNull String serverId,
            @NotNull String id,
            @NotNull String name
    ) {
        super(client, gson, serverId, id);
        this.name = Objects.requireNonNull(name, "name");
    }

    @Override
    protected String getMethod() {
        return "PUT";
    }

    @Override
    protected TypeToken<APIResponse<Backup>> getType() {
        return new TypeToken<APIResponse<Backup>>(){};
    }

    @Override
    protected HttpRequest.BodyPublisher getBodyPublisher(Gson gson, HttpRequest.Builder builder) {
        HashMap<String, String> body = new HashMap<>();
        body.put("name", this.name);
        return jsonBodyPublisher(gson, builder, body);
    }
}
