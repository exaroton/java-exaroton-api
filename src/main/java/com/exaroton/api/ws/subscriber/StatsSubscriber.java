package com.exaroton.api.ws.subscriber;

import com.exaroton.api.ws.data.StatsData;

public interface StatsSubscriber {

    /**
     * handle new stats
     * @param stats stats
     */
    void handleStats(StatsData stats);
}
