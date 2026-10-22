package com.exaroton.api.request.server;

import com.exaroton.api.ExarotonClient;
import com.exaroton.api.util.ParameterValidator;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public abstract class ServerSoftwareRequest<Datatype> extends ServerRequest<Datatype> {
    private final String softwareId;

    public ServerSoftwareRequest(
            @NotNull ExarotonClient client,
            @NotNull Gson gson,
            @NotNull String serverId,
            @NotNull String softwareId
    ) {
        super(client, gson, serverId);
        this.softwareId = ParameterValidator.requireValidId(softwareId);
    }

    @Override
    protected String getEndpoint() {
        return "servers/{id}/software/{software}/";
    }

    @Override
    protected HashMap<String, String> getData() {
        HashMap<String, String> map = super.getData();
        map.put("software", this.softwareId);
        return map;
    }
}
