package com.exaroton.api.ws.stream;

import com.exaroton.api.ws.WSClient;
import com.exaroton.api.ws.subscriber.ConsoleSubscriber;

public class ConsoleStream extends Stream<ConsoleSubscriber> {

    public ConsoleStream(WSClient client) {
        super(client);
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
