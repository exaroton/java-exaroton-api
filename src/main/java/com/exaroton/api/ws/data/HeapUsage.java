package com.exaroton.api.ws.data;

public class HeapUsage {
    /**
     * heap usage in bytes
     */
    private final long usage;

    public HeapUsage(long usage) {
        this.usage = usage;
    }

    /**
     * get heap usage
     * @return used heap in bytes
     */
    public long getUsage() {
        return usage;
    }
}
