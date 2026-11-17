package com.exaroton.api.server.backups;

import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

/**
 * A backup account (cloud storage like Google Drive, Dropbox or OneDrive) linked to a server. Backup accounts can't
 * be linked via the API, link them in the exaroton panel.
 *
 * @see com.exaroton.api.server.Server#getBackupAccounts()
 */
@SuppressWarnings("unused")
@ApiStatus.AvailableSince("2.6.0")
public final class BackupAccount {
    /**
     * Unique backup account ID
     */
    private String id;

    /**
     * Display name of the backup account
     */
    private String name;

    /**
     * Cloud storage provider of the backup account
     */
    private String provider;

    /**
     * Storage information
     */
    private Storage storage;

    /**
     * @return unique backup account ID
     */
    public String getId() {
        return id;
    }

    /**
     * @return display name of the backup account (e.g. example@gmail.com)
     */
    public String getName() {
        return name;
    }

    /**
     * @return cloud storage provider of the backup account (google-drive, dropbox or onedrive)
     */
    public String getProvider() {
        return provider;
    }

    /**
     * @return used storage in bytes or empty if unknown
     */
    public Optional<Long> getUsedStorage() {
        return Optional.ofNullable(storage).map(s -> s.used);
    }

    /**
     * @return storage limit in bytes or empty if unknown
     */
    public Optional<Long> getStorageLimit() {
        return Optional.ofNullable(storage).map(s -> s.limit);
    }

    private static final class Storage {
        private Long used;
        private Long limit;
    }
}
