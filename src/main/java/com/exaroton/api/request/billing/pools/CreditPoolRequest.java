package com.exaroton.api.request.billing.pools;

import com.exaroton.api.APIRequest;
import com.exaroton.api.ParameterValidator;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public abstract class CreditPoolRequest<T> extends APIRequest<T> {
    private final String poolId;

    protected CreditPoolRequest(@NotNull String id) {
        this.poolId = ParameterValidator.requireValidId(id);
    }

    @Override
    protected HashMap<String, String> getData() {
        HashMap<String, String> map = super.getData();
        map.put("id", this.poolId);
        return map;
    }
}
