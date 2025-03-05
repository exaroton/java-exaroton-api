package com.exaroton.api.request.billing.pools;

import com.exaroton.api.APIRequest;
import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.billing.pools.CreditPool;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

public class GetCreditPoolsRequest extends APIRequest<CreditPool[]> {
    public GetCreditPoolsRequest(@NotNull ExarotonClient client, @NotNull Gson gson) {
        super(client, gson);
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
