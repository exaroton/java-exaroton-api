package com.exaroton.api.ws.stream;

import com.exaroton.api.ws.WSClient;
import com.exaroton.api.ws.subscriber.TickSubscriber;

public class TickStream extends Stream<TickSubscriber> {

    public TickStream(WSClient client) {
        super(client);
    }

    @Override
    protected String getName() {
        return "tick";
    }
}
