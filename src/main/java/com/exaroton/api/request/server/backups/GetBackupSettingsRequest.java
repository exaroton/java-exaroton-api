package com.exaroton.api.request.server.backups;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.request.server.ServerRequest;
import com.exaroton.api.server.backups.BackupSettings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

public class GetBackupSettingsRequest extends ServerRequest<BackupSettings> {
    public GetBackupSettingsRequest(@NotNull ExarotonClient client, @NotNull Gson gson, @NotNull String serverId) {
        super(client, gson, serverId);
    }

    @Override
    protected String getEndpoint() {
        return "servers/{id}/backup-settings/";
    }

    @Override
    protected TypeToken<APIResponse<BackupSettings>> getType() {
        return new TypeToken<APIResponse<BackupSettings>>(){};
    }
}
