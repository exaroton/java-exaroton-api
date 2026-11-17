package com.exaroton.api.request.server.backups;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.request.server.ServerRequest;
import com.exaroton.api.server.backups.BackupAccount;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GetBackupAccountsRequest extends ServerRequest<List<BackupAccount>> {
    public GetBackupAccountsRequest(@NotNull ExarotonClient client, @NotNull Gson gson, @NotNull String serverId) {
        super(client, gson, serverId);
    }

    @Override
    protected String getEndpoint() {
        return "servers/{id}/backup-accounts/";
    }

    @Override
    protected TypeToken<APIResponse<List<BackupAccount>>> getType() {
        return new TypeToken<APIResponse<List<BackupAccount>>>(){};
    }
}
