package com.exaroton.api.request.server;


import com.exaroton.api.APIRequest;
import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.Server;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GetServersRequest extends APIRequest<List<Server>> {

    public GetServersRequest(@NotNull ExarotonClient client, @NotNull Gson gson) {
        super(client, gson);
    }

    @Override
    protected String getEndpoint() {
        return "servers/";
    }

    @Override
    protected TypeToken<APIResponse<List<Server>>> getType() {
        return new TypeToken<APIResponse<List<Server>>>(){};
    }
}
