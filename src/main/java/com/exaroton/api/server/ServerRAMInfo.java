package com.exaroton.api.server;

public class ServerRAMInfo {
    /**
     * Server RAM in GB
     */
    private final int ram;

    public ServerRAMInfo(int ram) {
        this.ram = ram;
    }

    /**
     * @return Server RAM in GB
     */
    public int getRam() {
        return ram;
    }
}
