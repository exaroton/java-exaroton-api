package com.exaroton.api.request.regions;

import com.exaroton.api.APIRequest;
import com.exaroton.api.APIResponse;
import com.exaroton.api.server.Region;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class GetRegionsRequest extends APIRequest<List<Region>> {
    @Override
    protected String getEndpoint() {
        return "regions/";
    }

    @Override
    protected TypeToken<APIResponse<List<Region>>> getType() {
        return new TypeToken<APIResponse<List<Region>>>(){};
    }
}
