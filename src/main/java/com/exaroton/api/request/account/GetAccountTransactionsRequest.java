package com.exaroton.api.request.account;

import com.exaroton.api.APIRequest;
import com.exaroton.api.APIResponse;
import com.exaroton.api.Page;
import com.exaroton.api.billing.transactions.Transaction;
import com.exaroton.api.util.ParameterValidator;
import com.google.gson.reflect.TypeToken;

public class GetAccountTransactionsRequest extends APIRequest<Page<Transaction>> {
    private final int page;

    public GetAccountTransactionsRequest(int page) {
        this.page = ParameterValidator.requirePositive(page, "page");
    }

    @Override
    protected String getEndpoint() {
        return "account/transactions/";
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
