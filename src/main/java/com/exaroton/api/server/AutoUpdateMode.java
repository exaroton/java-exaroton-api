package com.exaroton.api.server;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.ApiStatus;

/**
 * Whether the server software is updated automatically
 *
 * @see ServerProperties
 */
@ApiStatus.AvailableSince("2.6.0")
public enum AutoUpdateMode {
    /**
     * Never update the software automatically
     */
    @SerializedName("never")
    NEVER,
    /**
     * Ask before updating the software
     */
    @SerializedName("ask")
    ASK,
    /**
     * Always update the software automatically
     */
    @SerializedName("always")
    ALWAYS,
}
