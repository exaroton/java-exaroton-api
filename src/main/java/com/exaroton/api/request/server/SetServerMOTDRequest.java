package com.exaroton.api.request.server;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.Parameter;
import com.exaroton.api.server.ServerMOTDInfo;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SetServerMOTDRequest extends ServerRequest<ServerMOTDInfo> {
    private final String motd;

    public SetServerMOTDRequest(ExarotonClient client, String id, String motd) {
        super(client, id);
        this.motd = motd;
    }

    @Override
    protected String getEndpoint() {
        return "servers/{id}/options/motd/";
    }

    @Override
    protected String getMethod() {
        return "POST";
    }

    @Override
    protected ArrayList<Parameter> getParameters() {
        ArrayList<Parameter> parameters = super.getParameters();
        parameters.add(new Parameter("motd", this.motd));
        return parameters;
    }

    @Override
    protected Type getType() {
        return new TypeToken<APIResponse<ServerMOTDInfo>>(){}.getType();
    }
}
