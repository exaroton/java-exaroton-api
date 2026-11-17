package com.exaroton.api.request.billing.pools;

import com.exaroton.api.APIResponse;
import com.exaroton.api.Page;
import com.exaroton.api.billing.transactions.Transaction;
import com.exaroton.api.util.ParameterValidator;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

public class GetCreditPoolTransactionsRequest extends CreditPoolRequest<Page<Transaction>> {
    private final int page;

    public GetCreditPoolTransactionsRequest(@NotNull String id, int page) {
        super(id);
        this.page = ParameterValidator.requirePositive(page, "page");
    }

    @Override
    protected String getEndpoint() {
        return "billing/pools/{id}/transactions/";
    }

    @Override
    protected String getPath() {
        return super.getPath() + "?page=" + page;
    }

    @Override
    protected TypeToken<APIResponse<Page<Transaction>>> getType() {
        return new TypeToken<APIResponse<Page<Transaction>>>(){};
    }
}
