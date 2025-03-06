package com.exaroton.api.server;

import org.jetbrains.annotations.ApiStatus;

public final class ServerSoftware {
    /**
     * Unique software ID
     */
    public final String id;

    /**
     * Software name
     */
    public final String name;

    /**
     * Software version
     */
    public final String version;

    @ApiStatus.Internal
    public ServerSoftware(String id, String name, String version) {
        this.id = id;
        this.name = name;
        this.version = version;
    }
}
