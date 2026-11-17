package com.exaroton.api.server.backups;

import com.exaroton.api.Page;
import org.jetbrains.annotations.ApiStatus;

/**
 * A page of the backups of a server
 *
 * @see com.exaroton.api.server.Server#getBackups(int)
 */
@SuppressWarnings("unused")
@ApiStatus.AvailableSince("2.6.0")
public final class BackupPage extends Page<Backup> {
    /**
     * Whether a backup is currently being created or restored on this server
     */
    private boolean running;

    /**
     * Check whether a backup is currently being created or restored on this server. Poll this to find out when
     * restoring a backup is done.
     *
     * @return true if a backup is currently being created or restored on this server
     */
    public boolean isRunning() {
        return running;
    }
}
