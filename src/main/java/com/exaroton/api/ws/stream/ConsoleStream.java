package com.exaroton.api.ws.stream;

import com.exaroton.api.ws.WSClient;
import com.exaroton.api.ws.subscriber.ConsoleSubscriber;

public class ConsoleStream extends Stream<ConsoleSubscriber> {

    public ConsoleStream(WSClient client) {
        super(client);
        //subscribe to new console lines
        this.send("start");
    }

    @Override
    String getName() {
        return "console";
    }
}