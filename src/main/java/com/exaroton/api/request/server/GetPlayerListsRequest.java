package com.exaroton.api.request.server;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class GetPlayerListsRequest extends ServerRequest<String[]> {

    public GetPlayerListsRequest(ExarotonClient client, String id) {
        super(client, id);
    }

    @Override
    protected String getEndpoint() {
        return "servers/{id}/playerlists/";
    }

    @Override
    protected Type getType() {
        return new TypeToken<APIResponse<String[]>>(){}.getType();
    }
}
