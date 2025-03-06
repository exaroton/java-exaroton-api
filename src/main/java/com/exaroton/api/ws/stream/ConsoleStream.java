package com.exaroton.api.ws.stream;

import com.exaroton.api.ws.WebSocketConnection;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

public class ConsoleStream extends Stream {

    public ConsoleStream(@NotNull WebSocketConnection ws, @NotNull Gson gson) {
        super(ws, gson, StreamName.CONSOLE);
    }

    /**
     * execute a command using the websocket
     * @param command minecraft command
     */
    public void executeCommand(String command) {
        this.send("command", command);
    }
}
