package com.exaroton.api.ws.stream;


import com.exaroton.api.ws.WebSocketManager;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

public class ServerStatusStream extends Stream {

    public ServerStatusStream(@NotNull WebSocketManager ws, @NotNull Gson gson) {
        super(ws, gson);
    }

    @Override
    protected String getName() {
        return "status";
    }
}
