package com.exaroton.api.ws.data;

public class StatsData {
    private final MemoryUsage memory;

    public StatsData(MemoryUsage memory) {
        this.memory = memory;
    }

    /**
     * get memory info
     * @return memory usage
     */
    public MemoryUsage getMemory() {
        return memory;
    }
}
