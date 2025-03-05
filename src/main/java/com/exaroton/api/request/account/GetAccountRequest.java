package com.exaroton.api.request.account;


import com.exaroton.api.APIRequest;
import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.account.Account;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

public class GetAccountRequest extends APIRequest<Account> {

    public GetAccountRequest(@NotNull ExarotonClient client, @NotNull Gson gson) {
        super(client, gson);
    }

    @Override
    protected String getEndpoint() {
        return "account/";
    }

    @Override
    protected Type getType() {
        return new TypeToken<APIResponse<Account>>(){}.getType();
    }
}
