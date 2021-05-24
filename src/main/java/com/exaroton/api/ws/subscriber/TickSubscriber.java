package com.exaroton.api.ws.subscriber;

import com.exaroton.api.ws.data.TickData;

public abstract class TickSubscriber {

    /**
     * handle tick
     * @param tick tick data
     */
    public abstract void tick(TickData tick);
}
