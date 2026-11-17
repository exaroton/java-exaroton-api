package com.exaroton.api.request.server.backups;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.request.server.ServerRequest;
import com.exaroton.api.server.backups.BackupSettings;
import com.exaroton.api.server.backups.BackupSettingsOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.net.http.HttpRequest;
import java.util.Objects;

public class UpdateBackupSettingsRequest extends ServerRequest<BackupSettings> {
    private final BackupSettingsOptions options;

    public UpdateBackupSettingsRequest(
            @NotNull ExarotonClient client,
            @NotNull Gson gson,
            @NotNull String serverId,
            @NotNull BackupSettingsOptions options
    ) {
        super(client, gson, serverId);
        this.options = Objects.requireNonNull(options, "options");
    }

    @Override
    protected String getEndpoint() {
        return "servers/{id}/backup-settings/";
    }

    @Override
    protected String getMethod() {
        return "POST";
    }

    @Override
    protected TypeToken<APIResponse<BackupSettings>> getType() {
        return new TypeToken<APIResponse<BackupSettings>>(){};
    }

    @Override
    protected HttpRequest.BodyPublisher getBodyPublisher(Gson gson, HttpRequest.Builder builder) {
        return jsonBodyPublisher(gson, builder, this.options);
    }
}
