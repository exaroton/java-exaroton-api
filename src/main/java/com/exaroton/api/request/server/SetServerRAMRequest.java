package com.exaroton.api.request.server;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.Parameter;
import com.exaroton.api.server.ServerRAMInfo;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SetServerRAMRequest extends ServerRequest<ServerRAMInfo> {
    private final int ram;

    public SetServerRAMRequest(ExarotonClient client, String id, int ram) {
        super(client, id);
        this.ram = ram;
    }

    @Override
    protected String getEndpoint() {
        return "servers/{id}/options/ram/";
    }

    @Override
    protected String getMethod() {
        return "POST";
    }

    @Override
    protected ArrayList<Parameter> getParameters() {
        ArrayList<Parameter> parameters = super.getParameters();
        parameters.add(new Parameter("ram", Integer.toString(this.ram)));
        return parameters;
    }

    @Override
    protected Type getType() {
        return new TypeToken<APIResponse<ServerRAMInfo>>(){}.getType();
    }
}
