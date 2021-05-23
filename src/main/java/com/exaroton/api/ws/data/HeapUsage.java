package com.exaroton.api.ws.data;

public class HeapUsage {
    /**
     * heap usage in bytes
     */
    private final int usage;

    public HeapUsage(int usage) {
        this.usage = usage;
    }

    /**
     * get heap usage
     * @return used heap in bytes
     */
    public int getUsage() {
        return usage;
    }


    public double inGiB() {
        double usage = this.usage;
        return usage / (Math.pow(1024, 3));
    }
}
