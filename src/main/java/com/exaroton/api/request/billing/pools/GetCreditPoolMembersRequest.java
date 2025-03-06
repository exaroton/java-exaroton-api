package com.exaroton.api.request.billing.pools;

import com.exaroton.api.APIResponse;
import com.exaroton.api.billing.pools.CreditPoolMember;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GetCreditPoolMembersRequest extends CreditPoolRequest<List<CreditPoolMember>> {
    public GetCreditPoolMembersRequest(@NotNull String id) {
        super(id);
    }

    @Override
    protected String getEndpoint() {
        return "billing/pools/{id}/members/";
    }

    @Override
    protected TypeToken<APIResponse<List<CreditPoolMember>>> getType() {
        return new TypeToken<APIResponse<List<CreditPoolMember>>>(){};
    }
}
