package com.exaroton.api.request.server;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.Parameter;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class AddPlayerListEntriesRequest extends ServerListRequest<String[]> {

    private final String[] entries;

    public AddPlayerListEntriesRequest(ExarotonClient client, String id, String list, String[] entries) {
        super(client, id, list);
        if (entries == null || entries.length == 0) throw new IllegalArgumentException("No entries provided");
        this.entries = entries;
    }

    @Override
    protected String getEndpoint() {
        return "servers/{id}/playerlists/{list}/";
    }

    @Override
    protected Type getType() {
        return new TypeToken<APIResponse>(){}.getType();
    }

    @Override
    protected String getMethod() {
        return "PUT";
    }

    @Override
    protected ArrayList<Parameter> getParameters() {
        ArrayList<Parameter> parameters = super.getParameters();
        for (String entry: entries) {
            parameters.add(new Parameter("entries[]", entry));
        }
        return parameters;
    }
}
