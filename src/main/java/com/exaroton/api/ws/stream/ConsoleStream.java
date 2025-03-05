package com.exaroton.api.ws.stream;

import com.exaroton.api.ws.WebSocketManager;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

public class ConsoleStream extends Stream {

    public ConsoleStream(@NotNull WebSocketManager ws, @NotNull Gson gson) {
        super(ws, gson);
    }

    @Override
    protected String getName() {
        return "console";
    }

    /**
     * execute a command using the websocket
     * @param command minecraft command
     */
    public void executeCommand(String command) {
        this.send("command", command);
    }
}
