package com.exaroton.api.request.account;

import com.exaroton.api.APIRequest;
import com.exaroton.api.APIResponse;
import com.exaroton.api.account.Account;
import com.google.gson.reflect.TypeToken;

public class GetAccountRequest extends APIRequest<Account> {
    @Override
    protected String getEndpoint() {
        return "account/";
    }

    @Override
    protected TypeToken<APIResponse<Account>> getType() {
        return new TypeToken<APIResponse<Account>>(){};
    }
}
