package com.exaroton.api.request.server.backups;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.request.server.ServerRequest;
import com.exaroton.api.server.backups.BackupPage;
import com.exaroton.api.util.ParameterValidator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

public class GetBackupsRequest extends ServerRequest<BackupPage> {
    private final int page;

    public GetBackupsRequest(@NotNull ExarotonClient client, @NotNull Gson gson, @NotNull String serverId, int page) {
        super(client, gson, serverId);
        this.page = ParameterValidator.requirePositive(page, "page");
    }

    @Override
    protected String getEndpoint() {
        return "servers/{id}/backups/";
    }

    @Override
    protected String getPath() {
        return super.getPath() + "?page=" + page;
    }

    @Override
    protected TypeToken<APIResponse<BackupPage>> getType() {
        return new TypeToken<APIResponse<BackupPage>>(){};
    }
}
