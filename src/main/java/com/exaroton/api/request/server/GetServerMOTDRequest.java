package com.exaroton.api.request.server;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.ServerMOTDInfo;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class GetServerMOTDRequest extends ServerRequest<ServerMOTDInfo> {

    public GetServerMOTDRequest(ExarotonClient client, String id) {
        super(client, id);
    }

    @Override
    protected String getEndpoint() {
        return "servers/{id}/options/motd/";
    }

    @Override
    protected Type getType() {
        return new TypeToken<APIResponse<ServerMOTDInfo>>(){}.getType();
    }
}
