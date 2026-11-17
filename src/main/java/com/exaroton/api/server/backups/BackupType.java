package com.exaroton.api.server.backups;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.ApiStatus;

/**
 * Whether a backup was created automatically or manually
 */
@ApiStatus.AvailableSince("2.6.0")
public enum BackupType {
    /**
     * The backup was created automatically and will be deleted automatically once there are too many automatic
     * backups (see {@link BackupSettings#getLimit()})
     */
    @SerializedName("automatic")
    AUTOMATIC,
    /**
     * The backup was created manually
     */
    @SerializedName("manual")
    MANUAL,
}
