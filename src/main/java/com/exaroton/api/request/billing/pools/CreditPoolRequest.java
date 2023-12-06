package com.exaroton.api.request.billing.pools;

import com.exaroton.api.APIRequest;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.billing.pools.CreditPool;

import java.util.HashMap;

public abstract class CreditPoolRequest<T> extends APIRequest<T> {
    private final String poolId;

    public CreditPoolRequest(ExarotonClient client, String id) {
        super(client);
        this.poolId = id;
    }

    @Override
    protected HashMap<String, String> getData() {
        HashMap<String, String> map = super.getData();
        map.put("id", this.poolId);
        return map;
    }
}
