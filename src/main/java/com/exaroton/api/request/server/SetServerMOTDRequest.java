package com.exaroton.api.request.server;

import com.exaroton.api.APIResponse;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.ServerMOTDInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.HashMap;

public class SetServerMOTDRequest extends ServerRequest<ServerMOTDInfo> {
    private final String motd;

    public SetServerMOTDRequest(
            @NotNull ExarotonClient client,
            @NotNull Gson gson,
            @NotNull String id,
            @NotNull String motd
    ) {
        super(client, gson, id);
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
    protected Type getType() {
        return new TypeToken<APIResponse<ServerMOTDInfo>>(){}.getType();
    }

    @Override
    protected Object getBody() {
        HashMap<String, Object> body = new HashMap<>();
        body.put("motd", this.motd);
        return body;
    }
}
