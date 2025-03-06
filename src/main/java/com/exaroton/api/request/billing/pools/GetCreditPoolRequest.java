package com.exaroton.api.request.billing.pools;

import com.exaroton.api.APIResponse;
import com.exaroton.api.billing.pools.CreditPool;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

public class GetCreditPoolRequest extends CreditPoolRequest<CreditPool> {
    public GetCreditPoolRequest(@NotNull String id) {
        super(id);
    }

    @Override
    protected String getEndpoint() {
        return "billing/pools/{id}/";
    }

    @Override
    protected TypeToken<APIResponse<CreditPool>> getType() {
        return new TypeToken<APIResponse<CreditPool>>(){};
    }
}
