package com.exaroton.api.server;

import org.jetbrains.annotations.ApiStatus;

public final class ServerMOTDInfo {
    /**
     * Server MOTD
     */
    private final String motd;

    @ApiStatus.Internal
    public ServerMOTDInfo(String motd) {
        this.motd = motd;
    }

    /**
     * @return server MOTD
     */
    public String getMotd() {
        return motd;
    }
}
