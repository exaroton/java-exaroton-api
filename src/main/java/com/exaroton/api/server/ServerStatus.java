package com.exaroton.api.server;

import com.exaroton.api.BrandColor;

import java.util.*;

/**
 * Server status
 */
@SuppressWarnings("unused")
public enum ServerStatus {
    OFFLINE(0, "Offline", BrandColor.DANGER),
    ONLINE(1, "Online", BrandColor.SUCCESS),
    STARTING(2, "Starting", BrandColor.LOADING),
    STOPPING(3, "Stopping", BrandColor.LOADING),
    RESTARTING(4, "Restarting", BrandColor.LOADING),
    SAVING(5, "Saving", BrandColor.LOADING),
    LOADING(6, "Loading", BrandColor.LOADING),
    CRASHED(7, "Crashed", BrandColor.DANGER),
    PENDING(8, "Pending", BrandColor.LOADING),
    TRANSFERRING(9, "Transferring", BrandColor.LOADING),
    PREPARING(10, "Preparing", BrandColor.LOADING),
    ;

    /**
     * Group of statuses that are considered offline. The server files are stored in the storage system and
     * transferring to/from the host machine has not started yet.
     */
    public static final Set<ServerStatus> GROUP_OFFLINE = Set.of(OFFLINE, PREPARING, CRASHED);

    /**
     * Group of statuses that are considered stopping. It is expected that these will eventually lead to the server
     * being offline or crashed.
     */
    public static final Set<ServerStatus> GROUP_STOPPING = Set.of(SAVING, STOPPING);

    private final int value;
    private final String name;
    private final BrandColor color;

    ServerStatus(int value, String name, BrandColor color) {
        this.value = value;
        this.name = name;
        this.color = color;
    }

    /**
     * Get the status by its numeric value
     * @param value numeric value
     * @return Empty if not found
     */
    public static Optional<ServerStatus> fromValue(int value) {
        for (ServerStatus status : values()) {
            if (status.value == value) {
                return Optional.of(status);
            }
        }
        return Optional.empty();
    }

    /**
     * Get the numeric value of the status
     * @return numeric value
     */
    public int getValue() {
        return value;
    }

    /**
     * Get the name of the status
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the color of the status
     * @return color
     */
    public BrandColor getColor() {
        return color;
    }
}
