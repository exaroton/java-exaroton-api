package com.exaroton.api.server.backups;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.ApiStatus;

/**
 * Status of a backup
 */
@ApiStatus.AvailableSince("2.6.0")
public enum BackupStatus {
    /**
     * The backup is being created. A backup that fails to be created is deleted.
     */
    @SerializedName("creating")
    CREATING,
    /**
     * The backup has been created
     */
    @SerializedName("created")
    CREATED,
}
