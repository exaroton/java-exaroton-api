package com.exaroton.api.ws.subscriber;

import com.exaroton.api.ws.data.TickData;

public interface TickSubscriber {

    /**
     * handle tick
     * @param tick tick data
     */
    void handleTickData(TickData tick);
}
