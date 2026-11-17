package com.exaroton.api.request.server.backups;

import com.exaroton.api.ExarotonClient;
import com.exaroton.api.request.server.ServerRequest;
import com.exaroton.api.util.ParameterValidator;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public abstract class BackupRequest<Datatype> extends ServerRequest<Datatype> {
    private final String backupId;

    public BackupRequest(
            @NotNull ExarotonClient client,
            @NotNull Gson gson,
            @NotNull String serverId,
            @NotNull String backupId
    ) {
        super(client, gson, serverId);
        this.backupId = ParameterValidator.requireNonEmpty(backupId, "backupId");
    }

    @Override
    protected String getEndpoint() {
        return "servers/{id}/backups/{backup}/";
    }

    @Override
    protected HashMap<String, String> getData() {
        HashMap<String, String> map = super.getData();
        map.put("backup", this.backupId);
        return map;
    }
}
