package com.exaroton.api.request.server;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.ServerSoftwareDetails;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

public class GetServerSoftwareRequest extends ServerSoftwareRequest<ServerSoftwareDetails> {

    public GetServerSoftwareRequest(
            @NotNull ExarotonClient client,
            @NotNull Gson gson,
            @NotNull String serverId,
            @NotNull String softwareId
    ) {
        super(client, gson, serverId, softwareId);
    }

    @Override
    protected TypeToken<APIResponse<ServerSoftwareDetails>> getType() {
        return new TypeToken<APIResponse<ServerSoftwareDetails>>(){};
    }
}
