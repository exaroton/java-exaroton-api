package com.exaroton.api.server;

public class ServerMOTDInfo {
    /**
     * Server MOTD
     */
    private final String motd;

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
