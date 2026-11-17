package com.exaroton.api.server;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.ApiStatus;

/**
 * Which players can start a server automatically by joining it
 *
 * @see ServerProperties
 */
@ApiStatus.AvailableSince("2.6.0")
public enum AutostartType {
    /**
     * All players
     */
    @SerializedName("any")
    ANY,
    /**
     * Only whitelisted players
     */
    @SerializedName("whitelisted")
    WHITELISTED,
    /**
     * Only operators
     */
    @SerializedName("ops")
    OPS,
}
