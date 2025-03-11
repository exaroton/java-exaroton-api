package com.exaroton.api.ws.data;

@SuppressWarnings("unused")
public final class HeapUsage {
    /**
     * heap usage in bytes
     */
    private long usage;

    /**
     * get heap usage
     * @return used heap in bytes
     */
    public long getUsage() {
        return usage;
    }
}
