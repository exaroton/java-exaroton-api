package com.exaroton.api.server;

@SuppressWarnings("unused")
public final class ServerSoftware {
    /**
     * Unique ID of the software version
     */
    private String id;

    /**
     * Software name
     */
    private String name;

    /**
     * Software version
     */
    private String version;

    /**
     * @return unique ID of the software version
     */
    public String getId() {
        return id;
    }

    /**
     * @return name of the software
     */
    public String getName() {
        return name;
    }

    /**
     * @return version of the software
     */
    public String getVersion() {
        return version;
    }
}
