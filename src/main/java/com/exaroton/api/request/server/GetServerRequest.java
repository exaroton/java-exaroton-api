package com.exaroton.api.request.server;


import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.Server;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class GetServerRequest extends ServerRequest<Server> {

    public GetServerRequest(ExarotonClient client, String id) {
        super(client, id);
    }

    @Override
    protected String getEndpoint() {
        return "servers/{id}/";
    }

    @Override
    protected Type getType() {
        return new TypeToken<APIResponse<Server>>(){}.getType();
    }
}
