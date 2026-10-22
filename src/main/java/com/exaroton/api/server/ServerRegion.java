package com.exaroton.api.server;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.ApiStatus;

/**
 * Region a server can be created in
 */
@ApiStatus.AvailableSince("2.5.0")
public enum ServerRegion {
    @SerializedName("eu")
    EU,
    @SerializedName("na")
    NA,
}
