package com.exaroton.api.request.server.backups;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.request.server.ServerRequest;
import com.exaroton.api.server.backups.Backup;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.http.HttpRequest;
import java.util.HashMap;

public class CreateBackupRequest extends ServerRequest<Backup> {
    private final @Nullable String name;
    private final @Nullable String backupAccount;

    public CreateBackupRequest(
            @NotNull ExarotonClient client,
            @NotNull Gson gson,
            @NotNull String serverId,
            @Nullable String name,
            @Nullable String backupAccount
    ) {
        super(client, gson, serverId);
        this.name = name;
        this.backupAccount = backupAccount;
    }

    @Override
    protected String getEndpoint() {
        return "servers/{id}/backups/";
    }

    @Override
    protected String getMethod() {
        return "POST";
    }

    @Override
    protected TypeToken<APIResponse<Backup>> getType() {
        return new TypeToken<APIResponse<Backup>>(){};
    }

    @Override
    protected HttpRequest.BodyPublisher getBodyPublisher(Gson gson, HttpRequest.Builder builder) {
        HashMap<String, String> body = new HashMap<>();
        if (name != null) {
            body.put("name", name);
        }
        if (backupAccount != null) {
            body.put("backupAccount", backupAccount);
        }
        return jsonBodyPublisher(gson, builder, body);
    }
}
