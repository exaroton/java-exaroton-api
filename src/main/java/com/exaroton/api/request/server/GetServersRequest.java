package com.exaroton.api.request.server;


import com.exaroton.api.APIRequest;
import com.exaroton.api.APIResponse;
import com.exaroton.api.server.Server;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class GetServersRequest extends APIRequest<List<Server>> {

    public GetServersRequest() {
        super();
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
