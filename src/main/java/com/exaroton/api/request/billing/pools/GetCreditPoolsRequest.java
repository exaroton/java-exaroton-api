package com.exaroton.api.request.billing.pools;

import com.exaroton.api.APIRequest;
import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.billing.pools.CreditPool;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class GetCreditPoolsRequest extends APIRequest<CreditPool[]> {
    public GetCreditPoolsRequest(ExarotonClient client) {
        super(client);
    }

    @Override
    protected String getEndpoint() {
        return "billing/pools/";
    }

    @Override
    protected Type getType() {
        return new TypeToken<APIResponse<CreditPool[]>>(){}.getType();
    }
}
