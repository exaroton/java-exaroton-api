package com.exaroton.api.request.billing.pools;

import com.exaroton.api.APIRequest;
import com.exaroton.api.APIResponse;
import com.exaroton.api.billing.pools.CreditPool;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class GetCreditPoolsRequest extends APIRequest<List<CreditPool>> {
    @Override
    protected String getEndpoint() {
        return "billing/pools/";
    }

    @Override
    protected TypeToken<APIResponse<List<CreditPool>>> getType() {
        return new TypeToken<APIResponse<List<CreditPool>>>(){};
    }
}
