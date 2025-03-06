package com.exaroton.api.server;

import org.jetbrains.annotations.ApiStatus;

public final class ServerRAMInfo {
    /**
     * Server RAM in GB
     */
    private final int ram;

    @ApiStatus.Internal
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
