package com.exaroton.api.ws.subscriber;

import com.exaroton.api.ws.data.HeapUsage;
import com.exaroton.api.ws.data.StatsData;

public abstract class StatsSubscriber {

    /**
     * handle new stats
     * @param stats stats
     */
    public abstract void stats(StatsData stats);
}
