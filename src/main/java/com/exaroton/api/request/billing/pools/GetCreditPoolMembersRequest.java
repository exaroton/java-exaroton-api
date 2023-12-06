package com.exaroton.api.request.billing.pools;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.billing.pools.CreditPoolMember;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class GetCreditPoolMembersRequest extends CreditPoolRequest<CreditPoolMember[]> {
    public GetCreditPoolMembersRequest(ExarotonClient client, String id) {
        super(client, id);
    }

    @Override
    protected String getEndpoint() {
        return "billing/pools/{id}/members/";
    }

    @Override
    protected Type getType() {
        return new TypeToken<APIResponse<CreditPoolMember[]>>(){}.getType();
    }
}
