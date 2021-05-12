package com.exaroton.api.request.server;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.ServerLog;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class GetServerLogsRequest extends ServerRequest<ServerLog> {

    public GetServerLogsRequest(ExarotonClient client, String id) {
        super(client, id);
    }

    @Override
    protected String getEndpoint() {
        return "servers/{id}/logs/";
    }

    @Override
    protected Type getType() {
        return new TypeToken<APIResponse<ServerLog>>(){}.getType();
    }
}
