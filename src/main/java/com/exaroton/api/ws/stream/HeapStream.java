package com.exaroton.api.ws.stream;

import com.exaroton.api.ws.WebSocketManager;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

public class HeapStream extends Stream {

    public HeapStream(@NotNull WebSocketManager ws, @NotNull Gson gson) {
        super(ws, gson);
    }

    @Override
    protected String getName() {
        return "heap";
    }
}
