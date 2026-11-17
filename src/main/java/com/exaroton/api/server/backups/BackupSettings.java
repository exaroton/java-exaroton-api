package com.exaroton.api.server.backups;

import org.jetbrains.annotations.ApiStatus;

import java.time.Duration;

/**
 * Automatic backup settings of a server
 *
 * @see com.exaroton.api.server.Server#getBackupSettings()
 * @see com.exaroton.api.server.Server#setBackupSettings(BackupSettingsOptions)
 */
@SuppressWarnings("unused")
@ApiStatus.AvailableSince("2.6.0")
public final class BackupSettings {
    /**
     * Whether automatic backups are enabled
     */
    private boolean enabled;

    /**
     * How many automatic backups are kept
     */
    private int limit;

    /**
     * Interval between automatic backups in seconds
     */
    private long interval;

    /**
     * Number of automatic backups that failed in a row since the last successful one
     */
    private int failed;

    /**
     * @return true if automatic backups are enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @return how many automatic backups are kept. Older automatic backups are deleted.
     */
    public int getLimit() {
        return limit;
    }

    /**
     * @return interval between automatic backups
     */
    public Duration getInterval() {
        return Duration.ofSeconds(interval);
    }

    /**
     * @return number of automatic backups that failed in a row since the last successful one
     */
    public int getFailed() {
        return failed;
    }
}
