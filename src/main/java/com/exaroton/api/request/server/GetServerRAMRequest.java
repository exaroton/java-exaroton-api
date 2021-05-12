package com.exaroton.api.request.server;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.ServerRAMInfo;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class GetServerRAMRequest extends ServerRequest<ServerRAMInfo> {

    public GetServerRAMRequest(ExarotonClient client, String id) {
        super(client, id);
    }

    @Override
    protected String getEndpoint() {
        return "servers/{id}/options/ram/";
    }

    @Override
    protected Type getType() {
        return new TypeToken<APIResponse<ServerRAMInfo>>(){}.getType();
    }
}
