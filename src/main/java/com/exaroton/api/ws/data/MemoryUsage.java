package com.exaroton.api.ws.data;

@SuppressWarnings("unused")
public final class MemoryUsage {
    private double percent;
    private long usage;

    /**
     * percent of assigned RAM that are used
     * @return used RAM%
     */
    public double getPercent() {
        return percent;
    }

    /**
     * total used RAM (in bytes)
     * @return ram usage
     */
    public long getUsage() {
        return usage;
    }
}
