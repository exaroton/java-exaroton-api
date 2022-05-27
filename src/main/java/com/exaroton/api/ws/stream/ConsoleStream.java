package com.exaroton.api.ws.stream;

import com.exaroton.api.ws.WebSocketManager;

public class ConsoleStream extends Stream {

    public ConsoleStream(WebSocketManager ws) {
        super(ws);
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
