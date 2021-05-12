package com.exaroton.api.request.server;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.MclogsData;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class ShareServerLogsRequest extends ServerRequest<MclogsData> {

    public ShareServerLogsRequest(ExarotonClient client, String id) {
        super(client, id);
    }

    @Override
    protected String getEndpoint() {
        return "servers/{id}/logs/share/";
    }

    @Override
    protected Type getType() {
        return new TypeToken<APIResponse<MclogsData>>(){}.getType();
    }
}
