package com.exaroton.api.server.backups;

import com.exaroton.api.ExarotonClient;
import com.exaroton.api.request.server.backups.*;
import com.exaroton.api.util.Initializable;
import com.exaroton.api.util.ParameterValidator;
import com.google.gson.Gson;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * A backup of a server stored in one of its backup accounts. All backup methods require the backups permission
 * (only relevant for servers accessed via Share Access).
 *
 * @see com.exaroton.api.server.Server#getBackups(int)
 * @see com.exaroton.api.server.Server#getBackup(String)
 */
@SuppressWarnings("unused")
@ApiStatus.AvailableSince("2.6.0")
public final class Backup implements Initializable {
    /**
     * Has this backup been fetched from the API yet
     */
    private transient boolean fetched;

    /**
     * Unique backup ID
     */
    private String id;

    /**
     * Name of the backup, null if it has the default name
     */
    @Nullable
    private String name;

    /**
     * Whether the backup was created automatically or manually
     */
    @Nullable
    private BackupType type;

    /**
     * Status of the backup
     */
    private BackupStatus status;

    /**
     * Unique ID of the server the backup was created from
     */
    private String server;

    /**
     * Unique ID of the software the server had when the backup was created
     */
    @Nullable
    private String software;

    /**
     * Size of the backup in bytes
     */
    @Nullable
    private Long size;

    /**
     * Unix timestamp of when the backup was created
     */
    @Nullable
    private Long created;

    /**
     * Unique ID of the backup account the backup is stored in
     */
    @Nullable
    private String backupAccount;

    /**
     * The client used to create this backup object
     */
    private transient ExarotonClient client;

    /**
     * gson instance
     */
    private transient Gson gson;

    /**
     * Use {@link com.exaroton.api.server.Server#getBackup(String)} instead of calling this constructor directly.
     *
     * @param client   exaroton client
     * @param gson     gson instance
     * @param serverId server ID
     * @param id       backup ID
     */
    @ApiStatus.Internal
    public Backup(@NotNull ExarotonClient client, @NotNull Gson gson, @NotNull String serverId, @NotNull String id) {
        this.client = Objects.requireNonNull(client);
        this.gson = Objects.requireNonNull(gson);
        this.server = ParameterValidator.requireValidId(serverId);
        this.id = ParameterValidator.requireNonEmpty(id, "id");
    }

    /**
     * @return unique backup ID
     */
    public String getId() {
        return id;
    }

    /**
     * Has this backup been fetched from the API yet. If this is false only the ID and server ID are known.
     *
     * @return has this backup been fetched from the API yet
     */
    public boolean isFetched() {
        return fetched;
    }

    /**
     * @return name of the backup or empty if it has the default name
     */
    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    /**
     * @return whether the backup was created automatically or manually
     */
    public Optional<BackupType> getType() {
        return Optional.ofNullable(type);
    }

    /**
     * @return status of the backup
     */
    public BackupStatus getStatus() {
        return status;
    }

    /**
     * @return unique ID of the server the backup was created from
     */
    public String getServerId() {
        return server;
    }

    /**
     * @return unique ID of the software the server had when the backup was created
     */
    public Optional<String> getSoftwareId() {
        return Optional.ofNullable(software);
    }

    /**
     * @return size of the backup in bytes
     */
    public Optional<Long> getSize() {
        return Optional.ofNullable(size);
    }

    /**
     * @return time the backup was created
     */
    public Optional<Instant> getCreated() {
        return Optional.ofNullable(created).map(Instant::ofEpochSecond);
    }

    /**
     * @return unique ID of the backup account the backup is stored in
     * @see com.exaroton.api.server.Server#getBackupAccounts()
     */
    public Optional<String> getBackupAccountId() {
        return Optional.ofNullable(backupAccount);
    }

    /**
     * Fetch the backup from the API. If creating the backup failed, the backup is deleted, so this fails while
     * polling a backup that failed to be created.
     *
     * @return the backup with updated properties
     * @throws IOException connection errors
     */
    public CompletableFuture<Backup> fetch() throws IOException {
        return client.request(new GetBackupRequest(client, gson, server, id))
                .thenApply(this::setFromObject);
    }

    /**
     * Rename the backup. Renaming an automatic backup turns it into a manual backup, so it is no longer deleted
     * automatically. This is not possible while the backup is being created.
     *
     * @param name new name of the backup (at most 512 bytes). An empty string resets it to the default name.
     * @return the backup with updated properties
     * @throws IOException connection errors
     */
    public CompletableFuture<Backup> rename(@NotNull String name) throws IOException {
        return client.request(new RenameBackupRequest(client, gson, server, id, name))
                .thenApply(this::setFromObject);
    }

    /**
     * Delete the backup from its backup account. This is not possible while the backup is being created.
     *
     * @return completable future that completes once the backup has been deleted
     * @throws IOException connection errors
     */
    public CompletableFuture<Void> delete() throws IOException {
        return client.request(new DeleteBackupRequest(client, gson, server, id));
    }

    /**
     * Start restoring the backup to the server, replacing its current files. The server has to be offline and the
     * backup has to be created. The backup is restored asynchronously: poll {@link BackupPage#isRunning()} to find
     * out when the restore is done and don't start the server before that.
     *
     * @return the backup with updated properties. The future completes once the restore has started.
     * @throws IOException connection errors
     */
    public CompletableFuture<Backup> restore() throws IOException {
        return client.request(new RestoreBackupRequest(client, gson, server, id))
                .thenApply(this::setFromObject);
    }

    /**
     * @return the exaroton client used for requests
     */
    public ExarotonClient getClient() {
        return client;
    }

    @ApiStatus.Internal
    @Override
    public void initialize(@NotNull ExarotonClient client, @NotNull Gson gson) {
        this.client = Objects.requireNonNull(client);
        this.gson = Objects.requireNonNull(gson);
        this.fetched = true;
    }

    /**
     * update properties from fetched object
     *
     * @param backup backup fetched from the API
     * @return updated backup object
     */
    private Backup setFromObject(Backup backup) {
        this.fetched = true;
        this.id = backup.id;
        this.name = backup.name;
        this.type = backup.type;
        this.status = backup.status;
        this.server = backup.server;
        this.software = backup.software;
        this.size = backup.size;
        this.created = backup.created;
        this.backupAccount = backup.backupAccount;
        return this;
    }
}
