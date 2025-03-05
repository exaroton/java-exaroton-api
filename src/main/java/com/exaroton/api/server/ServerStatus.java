package com.exaroton.api.server;

import com.exaroton.api.BrandColor;

import java.util.Optional;

/**
 * Server status
 */
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
