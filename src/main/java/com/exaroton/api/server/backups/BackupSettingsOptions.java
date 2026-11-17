package com.exaroton.api.server.backups;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

/**
 * Changes to the automatic backup settings of a server. Settings that are not set are not changed.
 *
 * @see com.exaroton.api.server.Server#setBackupSettings(BackupSettingsOptions)
 */
@SuppressWarnings("unused")
@ApiStatus.AvailableSince("2.6.0")
public final class BackupSettingsOptions {
    /**
     * Whether automatic backups are enabled
     */
    @Nullable
    private Boolean enabled;

    /**
     * How many automatic backups are kept
     */
    @Nullable
    private Integer limit;

    /**
     * Interval between automatic backups in seconds
     */
    @Nullable
    private Long interval;

    /**
     * Create empty options. Use the setters to set the settings you want to change.
     */
    public BackupSettingsOptions() {
    }

    /**
     * @param enabled whether automatic backups are enabled
     * @return this
     */
    public BackupSettingsOptions setEnabled(@Nullable Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    /**
     * @param limit how many automatic backups are kept (2 - 1000). Older automatic backups are deleted.
     * @return this
     */
    public BackupSettingsOptions setLimit(@Nullable Integer limit) {
        this.limit = limit;
        return this;
    }

    /**
     * Set the interval between automatic backups. Supported intervals are 6 hours, 12 hours, 1 day and 2 days.
     *
     * @param interval interval between automatic backups
     * @return this
     */
    public BackupSettingsOptions setInterval(@Nullable Duration interval) {
        this.interval = interval == null ? null : interval.getSeconds();
        return this;
    }

    /**
     * @return whether automatic backups are enabled or null if this is not changed
     */
    public @Nullable Boolean getEnabled() {
        return enabled;
    }

    /**
     * @return how many automatic backups are kept or null if this is not changed
     */
    public @Nullable Integer getLimit() {
        return limit;
    }

    /**
     * @return interval between automatic backups or null if this is not changed
     */
    public @Nullable Duration getInterval() {
        return interval == null ? null : Duration.ofSeconds(interval);
    }
}
