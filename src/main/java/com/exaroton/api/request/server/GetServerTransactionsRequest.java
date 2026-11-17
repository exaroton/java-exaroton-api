package com.exaroton.api.request.server;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.Page;
import com.exaroton.api.billing.transactions.Transaction;
import com.exaroton.api.util.ParameterValidator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

public class GetServerTransactionsRequest extends ServerRequest<Page<Transaction>> {
    private final int page;

    public GetServerTransactionsRequest(@NotNull ExarotonClient client, @NotNull Gson gson, @NotNull String id, int page) {
        super(client, gson, id);
        this.page = ParameterValidator.requirePositive(page, "page");
    }

    @Override
    protected String getEndpoint() {
        return "servers/{id}/transactions/";
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
