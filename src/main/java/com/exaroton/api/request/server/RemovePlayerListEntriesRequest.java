package com.exaroton.api.request.server;

import com.exaroton.api.ExarotonClient;

public class RemovePlayerListEntriesRequest extends AddPlayerListEntriesRequest {

    public RemovePlayerListEntriesRequest(ExarotonClient client, String id, String list, String[] entries) {
        super(client, id, list, entries);
    }

    @Override
    protected String getMethod() {
        return "DELETE";
    }

}
