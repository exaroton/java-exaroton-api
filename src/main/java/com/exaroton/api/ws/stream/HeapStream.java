package com.exaroton.api.ws.stream;

import com.exaroton.api.ws.WSClient;
import com.exaroton.api.ws.subscriber.ConsoleSubscriber;
import com.exaroton.api.ws.subscriber.HeapSubscriber;

public class HeapStream extends Stream<HeapSubscriber> {

    public HeapStream(WSClient client) {
        super(client);
    }

    @Override
    protected String getName() {
        return "heap";
    }
}
