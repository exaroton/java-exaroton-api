package com.exaroton.api.request.server.backups;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.backups.Backup;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

public class GetBackupRequest extends BackupRequest<Backup> {
    public GetBackupRequest(@NotNull ExarotonClient client, @NotNull Gson gson, @NotNull String serverId, @NotNull String id) {
        super(client, gson, serverId, id);
    }

    @Override
    protected TypeToken<APIResponse<Backup>> getType() {
        return new TypeToken<APIResponse<Backup>>(){};
    }
}
