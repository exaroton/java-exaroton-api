package com.exaroton.api.request.billing.pools;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.billing.pools.CreditPool;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

public class GetCreditPoolRequest extends CreditPoolRequest<CreditPool> {
    public GetCreditPoolRequest(@NotNull ExarotonClient client, @NotNull Gson gson, @NotNull String id) {
        super(client, gson, id);
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
