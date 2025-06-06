package com.exaroton.api.request.billing.pools;

import com.exaroton.api.APIResponse;
import com.exaroton.api.server.Server;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GetCreditPoolServersRequest extends CreditPoolRequest<List<Server>> {
    public GetCreditPoolServersRequest(@NotNull String id) {
        super(id);
    }

    @Override
    protected String getEndpoint() {
        return "billing/pools/{id}/servers/";
    }

    @Override
    protected TypeToken<APIResponse<List<Server>>> getType() {
        return new TypeToken<APIResponse<List<Server>>>(){};
    }
}
