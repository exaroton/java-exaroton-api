package com.exaroton.api.request.billing.pools;

import com.exaroton.api.APIRequest;
import com.exaroton.api.ParameterValidator;
import com.exaroton.api.ExarotonClient;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public abstract class CreditPoolRequest<T> extends APIRequest<T> {
    private final String poolId;

    protected CreditPoolRequest(@NotNull ExarotonClient client, @NotNull Gson gson, @NotNull String id) {
        super(client, gson);
        this.poolId = ParameterValidator.requireValidId(id);
    }

    @Override
    protected HashMap<String, String> getData() {
        HashMap<String, String> map = super.getData();
        map.put("id", this.poolId);
        return map;
    }
}
