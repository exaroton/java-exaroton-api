package com.exaroton.api.request.server;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class GetPlayerListEntriesRequest extends ServerListRequest<String[]> {

    public GetPlayerListEntriesRequest(ExarotonClient client, String id, String list) {
        super(client, id, list);
    }

    @Override
    protected String getEndpoint() {
        return "servers/{id}/playerlists/{list}/";
    }

    @Override
    protected Type getType() {
        return new TypeToken<APIResponse<String[]>>(){}.getType();
    }
}
