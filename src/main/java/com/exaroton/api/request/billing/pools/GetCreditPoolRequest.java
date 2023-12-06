package com.exaroton.api.request.billing.pools;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.billing.pools.CreditPool;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class GetCreditPoolRequest extends CreditPoolRequest<CreditPool> {
    public GetCreditPoolRequest(ExarotonClient client, String id) {
        super(client, id);
    }

    @Override
    protected String getEndpoint() {
        return "billing/pools/{id}/";
    }

    @Override
    protected Type getType() {
        return new TypeToken<APIResponse<CreditPool>>(){}.getType();
    }
}
