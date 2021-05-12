package com.exaroton.api.request.server;


import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.Server;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class StopServerRequest extends ServerRequest<Server> {

    public StopServerRequest(ExarotonClient client, String id) {
        super(client, id);
    }

    @Override
    protected String getEndpoint() {
        return "servers/{id}/stop/";
    }

    @Override
    protected Type getType() {
        return new TypeToken<APIResponse>(){}.getType();
    }
}
