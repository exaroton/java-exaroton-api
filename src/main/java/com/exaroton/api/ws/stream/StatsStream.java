package com.exaroton.api.ws.stream;

import com.exaroton.api.ws.WSClient;
import com.exaroton.api.ws.subscriber.StatsSubscriber;

public class StatsStream extends Stream<StatsSubscriber> {

    public StatsStream(WSClient client) {
        super(client);
    }

    @Override
    protected String getName() {
        return "stats";
    }
}
