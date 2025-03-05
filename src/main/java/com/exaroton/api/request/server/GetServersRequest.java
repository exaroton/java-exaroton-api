package com.exaroton.api.request.server;


import com.exaroton.api.APIRequest;
import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.Server;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

public class GetServersRequest extends APIRequest<Server[]> {

    public GetServersRequest(@NotNull ExarotonClient client, @NotNull Gson gson) {
        super(client, gson);
    }

    @Override
    protected String getEndpoint() {
        return "servers/";
    }

    @Override
    protected Type getType() {
        return new TypeToken<APIResponse<Server[]>>(){}.getType();
    }
}
