package com.exaroton.api.ws.data;

public class MemoryUsage {
    private double percent;
    private int usage;

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
    public int getUsage() {
        return usage;
    }
}
