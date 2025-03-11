package com.exaroton.api.ws.data;

@SuppressWarnings("unused")
public final class TickData {
    private double averageTickTime;

    /**
     * get average tick time
     * @return average tick time in ms
     */
    public double getAverageTickTime() {
        return averageTickTime;
    }

    /**
     * calculate tps (ticks per second)
     * 1000 / tick time (limit = 20)
     * @return calculated tps
     */
    public double calculateTPS() {
        if (this.averageTickTime < 50) return 20;
        return 1000 / this.averageTickTime;
    }
}
