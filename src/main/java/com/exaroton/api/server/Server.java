package com.exaroton.api.server;

import com.exaroton.api.ExarotonClient;
import com.exaroton.api.Page;
import com.exaroton.api.billing.transactions.Transaction;
import com.exaroton.api.request.server.access.AddAccessRequest;
import com.exaroton.api.request.server.access.GetAccessListRequest;
import com.exaroton.api.request.server.backups.CreateBackupRequest;
import com.exaroton.api.request.server.backups.GetBackupAccountsRequest;
import com.exaroton.api.request.server.backups.GetBackupSettingsRequest;
import com.exaroton.api.request.server.backups.GetBackupsRequest;
import com.exaroton.api.request.server.backups.UpdateBackupSettingsRequest;
import com.exaroton.api.request.server.domains.AddDomainRequest;
import com.exaroton.api.request.server.domains.GetDomainsRequest;
import com.exaroton.api.server.access.ServerAccess;
import com.exaroton.api.server.backups.*;
import com.exaroton.api.server.domains.Domain;
import com.exaroton.api.util.Initializable;
import com.exaroton.api.util.ParameterValidator;
import com.exaroton.api.request.server.*;
import com.exaroton.api.software.Software;
import com.exaroton.api.ws.WebSocketConnection;
import com.exaroton.api.ws.stream.*;
import com.exaroton.api.ws.subscriber.*;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@SuppressWarnings("unused")
public final class Server implements Initializable {

    /**
     * has this server been fetched from the API yet
     */
    private boolean fetched;

    /**
     * Unique server ID
     */
    private String id;

    /**
     * Server  name
     */
    private String name;

    /**
     * Full server address (e.g. example.exaroton.me)
     */
    private String address;

    /**
     * Server MOTD
     */
    private String motd;

    /**
     * Server status code
     * see StatusCode
     */
    private int status;

    /**
     * Information about players
     */
    private PlayerInfo players;

    /**
     * Host address. Only available if the server is online
     */
    @Nullable
    private String host;

    /**
     * Server port. This might not be available if the server was just created.
     */
    @Nullable
    private Integer port;

    /**
     * Information about the installed server software
     */
    private ServerSoftware software;

    /**
     * Whether the server is accessed via the Share Access feature
     */
    private boolean shared;

    /**
     * The permissions your account has on this server via the Share Access feature. Null if you are the owner.
     */
    @Nullable
    private List<String> permissions;

    /**
     * ID of the region the server is located in
     */
    @Nullable
    private String region;

    /**
     * the client used to create this server
     */
    private transient ExarotonClient client;

    /**
     * web socket client
     */
    @Nullable
    private transient WebSocketConnection webSocket;

    /**
     * gson instance
     */
    private transient Gson gson;

    /**
     * Create a new server object
     *
     * @param client exaroton client that will be used for requests
     * @param gson   gson instance
     * @param id     server id
     */
    @ApiStatus.Internal
    public Server(@NotNull ExarotonClient client, @NotNull Gson gson, @NotNull String id) {
        this.client = Objects.requireNonNull(client);
        this.gson = Objects.requireNonNull(gson);
        this.id = Objects.requireNonNull(id);
    }

    /**
     * Get the server id
     *
     * @return unique server id (tgkm731xO7GiHt76)
     */
    public String getId() {
        return id;
    }

    /**
     * Check if this server has been fetched from the API.
     * If not, some methods may not work as expected.
     *
     * @return true if the server has been fetched
     */
    public boolean isFetched() {
        return fetched;
    }

    /**
     * Get the server name
     *
     * @return unique server name (example)
     */
    public String getName() {
        return name;
    }

    /**
     * Get the server address
     *
     * @return unique server address (example.exaroton.com)
     */
    public String getAddress() {
        return address;
    }

    /**
     * Get the current server status
     *
     * @return status or OFFLINE if the status is unknown
     */
    public ServerStatus getStatus() {
        return ServerStatus.fromValue(status).orElse(ServerStatus.OFFLINE);
    }

    /**
     * check if the server has this status
     *
     * @param status status
     * @return true if the status matches
     */
    public boolean hasStatus(@NotNull ServerStatus... status) {
        return hasStatus(Set.of(Objects.requireNonNull(status)));
    }

    /**
     * check if the server has one of the given statuses
     *
     * @param status status
     * @return true if the status matches
     */
    public boolean hasStatus(@NotNull Set<ServerStatus> status) {
        return Objects.requireNonNull(status).contains(this.getStatus());
    }

    /**
     * Get the MOTD (message of the day)
     *
     * @return server MOTD
     */
    public String getMotd() {
        return motd;
    }

    /**
     * Fetch the MOTD from the API
     * To retrieve the cached MOTD use {@link #getMotd()}
     *
     * @return server MOTD
     * @throws IOException connection errors
     */
    public CompletableFuture<ServerMOTDInfo> fetchMotd() throws IOException {
        return client.request(new GetServerMOTDRequest(this.client, this.gson, this.id))
                .thenApply(data -> {
                    this.motd = data.getMotd();
                    return data;
                });
    }

    /**
     * Set the server MOTD
     *
     * @param motd new server MOTD
     * @return updated server MOTD
     * @throws IOException connection errors
     */
    public CompletableFuture<ServerMOTDInfo> setMotd(@NotNull String motd) throws IOException {
        return client.request(new SetServerMOTDRequest(this.client, this.gson, this.id, Objects.requireNonNull(motd)))
                .thenApply(data -> {
                    this.motd = data.getMotd();
                    return data;
                });
    }

    /**
     * Get player info
     *
     * @return server player info
     */
    public PlayerInfo getPlayerInfo() {
        return players;
    }

    /**
     * Get the host
     * (Only available if the server is online)
     *
     * @return host server address
     */
    public Optional<String> getHost() {
        return Optional.ofNullable(host);
    }

    /**
     * Get the Server port. This might not be available if the server was just created.
     *
     * @return server port
     */
    public Optional<Integer> getPort() {
        return Optional.ofNullable(port);
    }

    /**
     * Returns an InetSocketAddress with the host and port of the server or an empty optional if the server is offline.
     *
     * @return InetSocketAddress with the host and port of the server
     */
    @ApiStatus.AvailableSince("2.2.0")
    public Optional<InetSocketAddress> getSocketAddress() {
        if (host == null || port == null) {
            return Optional.empty();
        }

        return Optional.of(new InetSocketAddress(host, port));
    }

    /**
     * Get the server software
     *
     * @return server software
     */
    public ServerSoftware getSoftware() {
        return software;
    }

    /**
     * Is the server shared
     *
     * @return server shared
     */
    public boolean isShared() {
        return shared;
    }

    /**
     * Get the permissions your account has on this server via the Share Access feature
     *
     * @return permissions (e.g. console, players) or empty if you are the owner of the server
     */
    @ApiStatus.AvailableSince("2.5.0")
    public Optional<List<String>> getPermissions() {
        return Optional.ofNullable(permissions);
    }

    /**
     * Check if your account has a permission on this server. The owner of a server has all permissions. This
     * requires the server to be fetched, otherwise the server is assumed to be owned by your account.
     *
     * @param permission permission (e.g. console, players)
     * @return true if your account owns the server or has the permission via the Share Access feature
     */
    @ApiStatus.AvailableSince("2.5.0")
    public boolean hasPermission(@NotNull String permission) {
        Objects.requireNonNull(permission);
        return !shared || (permissions != null && permissions.contains(permission));
    }

    /**
     * Get the ID of the region the server is located in
     *
     * @return region ID (e.g. eu) or null if the server hasn't been fetched yet
     * @see ExarotonClient#getRegions()
     * @see ServerRegion#fromId(String)
     */
    @ApiStatus.AvailableSince("2.6.0")
    public @Nullable String getRegion() {
        return region;
    }

    /**
     * get the exaroton client used to create this server
     *
     * @return exaroton client used to create this server
     */
    public ExarotonClient getClient() {
        return client;
    }

    /**
     * Fetch the server from the API
     *
     * @param force always fetch the server even if it has already been fetched
     * @return the server object with updated properties
     * @throws IOException connection errors
     */
    public CompletableFuture<Server> fetch(boolean force) throws IOException {
        if (!force && isFetched()) {
            return CompletableFuture.completedFuture(this);
        }

        return client.request(new GetServerRequest(this.client, this.gson, this.id))
                .thenApply(this::setFromObject);
    }

    public CompletableFuture<Server> fetch() throws IOException {
        return fetch(true);
    }

    /**
     * Permanently delete the server including all of its files. This is only possible for the owner of the server
     * (not via Share Access) and only while the server is offline or crashed. If a websocket connection is open,
     * it will be closed once the server has been deleted.
     *
     * @return completable future that completes once the server has been deleted
     * @throws IOException connection errors
     */
    @ApiStatus.AvailableSince("2.5.0")
    public CompletableFuture<Void> delete() throws IOException {
        return client.request(new DeleteServerRequest(this.client, this.gson, this.id))
                .thenRun(this::unsubscribe);
    }

    /**
     * Get information about installing a specific software on this server, e.g. whether it requires a reinstall
     *
     * @param softwareId software id (see {@link ExarotonClient#getSoftwareList()})
     * @return software details
     * @throws IOException connection errors
     */
    @ApiStatus.AvailableSince("2.5.0")
    public CompletableFuture<ServerSoftwareDetails> getSoftwareDetails(@NotNull String softwareId) throws IOException {
        return client.request(new GetServerSoftwareRequest(this.client, this.gson, this.id, softwareId));
    }

    /**
     * Get information about installing a specific software on this server, e.g. whether it requires a reinstall
     *
     * @param software software (see {@link ExarotonClient#getSoftwareList()})
     * @return software details
     * @throws IOException connection errors
     */
    @ApiStatus.AvailableSince("2.5.0")
    public CompletableFuture<ServerSoftwareDetails> getSoftwareDetails(@NotNull Software software) throws IOException {
        return getSoftwareDetails(Objects.requireNonNull(software).getId());
    }

    /**
     * Install a software on this server. Equivalent to {@link #installSoftware(String, boolean)} with
     * reinstall = false. This fails if the software requires a reinstall.
     *
     * @param softwareId software id (see {@link ExarotonClient#getSoftwareList()})
     * @return completable future that completes once the request has been sent
     * @throws IOException connection errors
     */
    @ApiStatus.AvailableSince("2.5.0")
    public CompletableFuture<Void> installSoftware(@NotNull String softwareId) throws IOException {
        return installSoftware(softwareId, false);
    }

    /**
     * Install a software on this server
     *
     * @param softwareId software id (see {@link ExarotonClient#getSoftwareList()})
     * @param reinstall  confirm the reinstall if the software requires one (see
     *                   {@link ServerSoftwareDetails#requiresReinstall()}). A reinstall deletes all server files!
     * @return completable future that completes once the request has been sent
     * @throws IOException connection errors
     */
    @ApiStatus.AvailableSince("2.5.0")
    public CompletableFuture<Void> installSoftware(@NotNull String softwareId, boolean reinstall) throws IOException {
        return client.request(new InstallServerSoftwareRequest(this.client, this.gson, this.id, softwareId, reinstall));
    }

    /**
     * Install a software on this server. Equivalent to {@link #installSoftware(Software, boolean)} with
     * reinstall = false. This fails if the software requires a reinstall.
     *
     * @param software software (see {@link ExarotonClient#getSoftwareList()})
     * @return completable future that completes once the request has been sent
     * @throws IOException connection errors
     */
    @ApiStatus.AvailableSince("2.5.0")
    public CompletableFuture<Void> installSoftware(@NotNull Software software) throws IOException {
        return installSoftware(software, false);
    }

    /**
     * Install a software on this server
     *
     * @param software  software (see {@link ExarotonClient#getSoftwareList()})
     * @param reinstall confirm the reinstall if the software requires one (see
     *                  {@link ServerSoftwareDetails#requiresReinstall()}). A reinstall deletes all server files!
     * @return completable future that completes once the request has been sent
     * @throws IOException connection errors
     */
    @ApiStatus.AvailableSince("2.5.0")
    public CompletableFuture<Void> installSoftware(@NotNull Software software, boolean reinstall) throws IOException {
        return installSoftware(Objects.requireNonNull(software).getId(), reinstall);
    }

    /**
     * Get the server properties (e.g. auto stop). Only properties that are available for this server and that your
     * account is allowed to change are included.
     *
     * @return server properties
     * @throws IOException connection errors
     */
    @ApiStatus.AvailableSince("2.6.0")
    public CompletableFuture<ServerProperties> getProperties() throws IOException {
        return client.request(new GetServerPropertiesRequest(this.client, this.gson, this.id));
    }

    /**
     * Change server properties. Properties that are not set are not changed. Either all properties are changed or
     * none: the request fails if any property is not available for this server, has an invalid value or your
     * account is missing the permission for it.
     *
     * @param properties properties to change
     * @return all server properties after the change
     * @throws IOException connection errors
     */
    @ApiStatus.AvailableSince("2.6.0")
    public CompletableFuture<ServerProperties> setProperties(@NotNull ServerProperties properties) throws IOException {
        return client.request(new SetServerPropertiesRequest(this.client, this.gson, this.id, properties));
    }

    /**
     * Change server properties by name. Use this for properties that are not supported by {@link ServerProperties}
     * yet. Properties that are not included are not changed.
     *
     * @param properties map of property name (e.g. autostop-time) to the new value
     * @return all server properties after the change
     * @throws IOException connection errors
     * @see #setProperties(ServerProperties)
     */
    @ApiStatus.AvailableSince("2.6.0")
    public CompletableFuture<ServerProperties> setProperties(@NotNull Map<String, Object> properties)
            throws IOException {
        return client.request(new SetServerPropertiesRequest(this.client, this.gson, this.id,
                ParameterValidator.requireNonEmpty(properties, "properties")));
    }

    /**
     * Transfer the server to another region. The server has to be offline. The transfer runs asynchronously: the
     * server has the status {@link ServerStatus#TRANSFERRING} while its files are copied, afterwards it returns to
     * its previous status with the new region. Use {@link #waitForStatus(ServerStatus...)} or {@link #fetch()} to
     * find out when the transfer is done. Transferring to the current region does nothing.
     *
     * @param region region ID (see {@link ExarotonClient#getRegions()})
     * @return this server with updated properties. The future completes once the transfer has started.
     * @throws IOException connection errors
     */
    @ApiStatus.AvailableSince("2.6.0")
    public CompletableFuture<Server> transfer(@NotNull String region) throws IOException {
        return client.request(new TransferServerRequest(this.client, this.gson, this.id, region))
                .thenApply(this::setFromObject);
    }

    /**
     * Transfer the server to another region
     *
     * @param region region
     * @return this server with updated properties. The future completes once the transfer has started.
     * @throws IOException connection errors
     * @see #transfer(String)
     */
    @ApiStatus.AvailableSince("2.6.0")
    public CompletableFuture<Server> transfer(@NotNull ServerRegion region) throws IOException {
        return transfer(Objects.requireNonNull(region).getId());
    }

    /**
     * Transfer the server to another region
     *
     * @param region region (see {@link ExarotonClient#getRegions()})
     * @return this server with updated properties. The future completes once the transfer has started.
     * @throws IOException connection errors
     * @see #transfer(String)
     */
    @ApiStatus.AvailableSince("2.6.0")
    public CompletableFuture<Server> transfer(@NotNull Region region) throws IOException {
        return transfer(Objects.requireNonNull(region).getId());
    }

    /**
     * Get the first page of the backups of this server, newest first
     *
     * @return first page of backups
     * @throws IOException connection errors
     * @see #getBackups(int)
     */
    @ApiStatus.AvailableSince("2.6.0")
    public CompletableFuture<BackupPage> getBackups() throws IOException {
        return getBackups(1);
    }

    /**
     * Get a page of the backups of this server in all of its backup accounts (20 per page), newest first. Requires
     * the backups permission.
     *
     * @param page page number starting at 1. Pages after the last page are empty.
     * @return page of backups
     * @throws IOException connection errors
     */
    @ApiStatus.AvailableSince("2.6.0")
    public CompletableFuture<BackupPage> getBackups(int page) throws IOException {
        return client.request(new GetBackupsRequest(this.client, this.gson, this.id, page));
    }

    /**
     * Get a backup. This method does not fetch the backup from the API.
     *
     * @param id backup id
     * @return empty backup object
     * @see Backup#fetch()
     */
    @ApiStatus.AvailableSince("2.6.0")
    public Backup getBackup(@NotNull String id) {
        return new Backup(this.client, this.gson, this.id, id);
    }

    /**
     * Start creating a backup with the default name in the first backup account linked to the server
     *
     * @return the new backup
     * @throws IOException connection errors
     * @see #createBackup(String, String)
     */
    @ApiStatus.AvailableSince("2.6.0")
    public CompletableFuture<Backup> createBackup() throws IOException {
        return createBackup(null, (String) null);
    }

    /**
     * Start creating a backup in the first backup account linked to the server
     *
     * @param name name of the backup (at most 512 bytes) or null for the default name
     * @return the new backup
     * @throws IOException connection errors
     * @see #createBackup(String, String)
     */
    @ApiStatus.AvailableSince("2.6.0")
    public CompletableFuture<Backup> createBackup(@Nullable String name) throws IOException {
        return createBackup(name, (String) null);
    }

    /**
     * Start creating a manual backup of the server. Backup accounts can only be linked in the exaroton panel. The
     * backup is created asynchronously: the returned backup has the status {@link BackupStatus#CREATING}. Use
     * {@link Backup#fetch()} to find out when it is done. If creating the backup fails, the backup is deleted.
     * Requires the backups permission.
     *
     * @param name            name of the backup (at most 512 bytes) or null for the default name
     * @param backupAccountId ID of the backup account to store the backup in (see {@link #getBackupAccounts()}) or
     *                        null for the first backup account linked to the server
     * @return the new backup
     * @throws IOException connection errors
     */
    @ApiStatus.AvailableSince("2.6.0")
    public CompletableFuture<Backup> createBackup(@Nullable String name, @Nullable String backupAccountId)
            throws IOException {
        return client.request(new CreateBackupRequest(this.client, this.gson, this.id, name, backupAccountId));
    }

    /**
     * Start creating a manual backup of the server
     *
     * @param name          name of the backup (at most 512 bytes) or null for the default name
     * @param backupAccount backup account to store the backup in (see {@link #getBackupAccounts()})
     * @return the new backup
     * @throws IOException connection errors
     * @see #createBackup(String, String)
     */
    @ApiStatus.AvailableSince("2.6.0")
    public CompletableFuture<Backup> createBackup(@Nullable String name, @NotNull BackupAccount backupAccount)
            throws IOException {
        return createBackup(name, Objects.requireNonNull(backupAccount).getId());
    }

    /**
     * Get the backup accounts (cloud storage like Google Drive, Dropbox or OneDrive) linked to the server. Backups
     * are created in the first one by default. Backup accounts can't be linked via the API. Requires the backups
     * permission.
     *
     * @return linked backup accounts
     * @throws IOException connection errors
     */
    @ApiStatus.AvailableSince("2.6.0")
    public CompletableFuture<List<BackupAccount>> getBackupAccounts() throws IOException {
        return client.request(new GetBackupAccountsRequest(this.client, this.gson, this.id));
    }

    /**
     * Get the automatic backup settings. Requires the backups permission.
     *
     * @return automatic backup settings
     * @throws IOException connection errors
     */
    @ApiStatus.AvailableSince("2.6.0")
    public CompletableFuture<BackupSettings> getBackupSettings() throws IOException {
        return client.request(new GetBackupSettingsRequest(this.client, this.gson, this.id));
    }

    /**
     * Change the automatic backup settings. Settings that are not set are not changed. Requires the backups
     * permission.
     *
     * @param options settings to change
     * @return automatic backup settings after the change
     * @throws IOException connection errors
     */
    @ApiStatus.AvailableSince("2.6.0")
    public CompletableFuture<BackupSettings> setBackupSettings(@NotNull BackupSettingsOptions options)
            throws IOException {
        return client.request(new UpdateBackupSettingsRequest(this.client, this.gson, this.id, options));
    }

    /**
     * Get the custom domains of this server. Requires the domains permission.
     *
     * @return custom domains
     * @throws IOException connection errors
     */
    @ApiStatus.AvailableSince("2.6.0")
    public CompletableFuture<List<Domain>> getDomains() throws IOException {
        return client.request(new GetDomainsRequest(this.client, this.gson, this.id))
                .thenApply(domains -> {
                    domains.forEach(domain -> domain.setServerId(this.id));
                    return domains;
                });
    }

    /**
     * Get a custom domain. This method does not fetch the domain from the API.
     *
     * @param id domain id
     * @return empty domain object
     * @see Domain#fetch()
     */
    @ApiStatus.AvailableSince("2.6.0")
    public Domain getDomain(@NotNull String id) {
        return new Domain(this.client, this.gson, this.id, id);
    }

    /**
     * Add a custom domain to this server (up to 10 per server). To link the domain, set its NS records at your DNS
     * provider to the {@link Domain#getNameservers() nameservers}. If they already point there, the domain is
     * linked immediately, otherwise use {@link Domain#check()} once they do. Requires the domains permission.
     *
     * @param domain the domain (e.g. play.example.com)
     * @return the new domain
     * @throws IOException connection errors
     */
    @ApiStatus.AvailableSince("2.6.0")
    public CompletableFuture<Domain> addDomain(@NotNull String domain) throws IOException {
        return client.request(new AddDomainRequest(this.client, this.gson, this.id, domain))
                .thenApply(result -> result.setServerId(this.id));
    }

    /**
     * Get the accounts this server is shared with via the Share Access feature, without the owner. Requires the
     * access permission.
     *
     * @return accounts with access to this server
     * @throws IOException connection errors
     */
    @ApiStatus.AvailableSince("2.6.0")
    public CompletableFuture<List<ServerAccess>> getAccessList() throws IOException {
        return client.request(new GetAccessListRequest(this.client, this.gson, this.id))
                .thenApply(list -> {
                    list.forEach(access -> access.setServerId(this.id));
                    return list;
                });
    }

    /**
     * Get the access of an account to this server. This method does not fetch the access from the API.
     *
     * @param id access id (see {@link ServerAccess#getId()})
     * @return empty access object
     * @see ServerAccess#fetch()
     */
    @ApiStatus.AvailableSince("2.6.0")
    public ServerAccess getAccess(@NotNull String id) {
        return new ServerAccess(this.client, this.gson, this.id, id);
    }

    /**
     * Share this server with another exaroton account with the default permissions (server)
     *
     * @param username name of the exaroton account
     * @return the new access
     * @throws IOException connection errors
     * @see #addAccess(String, Collection)
     */
    @ApiStatus.AvailableSince("2.6.0")
    public CompletableFuture<ServerAccess> addAccess(@NotNull String username) throws IOException {
        return addAccess(username, null);
    }

    /**
     * Share this server with another exaroton account. Fails if the server is already shared with this account
     * (change its permissions with {@link ServerAccess#setPermissions(Collection)} instead). Requires the access
     * permission.
     *
     * @param username    name of the exaroton account
     * @param permissions permissions of the account (see {@link ServerAccess}) or null for the default permissions
     * @return the new access
     * @throws IOException connection errors
     */
    @ApiStatus.AvailableSince("2.6.0")
    public CompletableFuture<ServerAccess> addAccess(@NotNull String username, @Nullable Collection<String> permissions)
            throws IOException {
        return client.request(new AddAccessRequest(this.client, this.gson, this.id, username, permissions))
                .thenApply(access -> access.setServerId(this.id));
    }

    /**
     * Get the first page of the transactions of this server
     *
     * @return first page of transactions
     * @throws IOException connection errors
     * @see #getTransactions(int)
     */
    @ApiStatus.AvailableSince("2.6.0")
    public CompletableFuture<Page<Transaction>> getTransactions() throws IOException {
        return getTransactions(1);
    }

    /**
     * Get a page of the transactions of this server (20 per page), newest first with active transactions before all
     * others. Credits are seen from the perspective of the server. Requires the transactions permission.
     *
     * @param page page number starting at 1. Pages after the last page are empty.
     * @return page of transactions
     * @throws IOException connection errors
     */
    @ApiStatus.AvailableSince("2.6.0")
    public CompletableFuture<Page<Transaction>> getTransactions(int page) throws IOException {
        return client.request(new GetServerTransactionsRequest(this.client, this.gson, this.id, page));
    }

    /**
     * Get the current server log
     *
     * @return server log
     * @throws IOException connection errors
     */
    public CompletableFuture<ServerLog> getLog() throws IOException {
        return client.request(new GetServerLogsRequest(this.client, this.gson, this.id));
    }

    /**
     * Share the server log to mclo.gs
     *
     * @return mclogs response
     * @throws IOException connection errors
     */
    public CompletableFuture<MclogsData> shareLog() throws IOException {
        return client.request(new ShareServerLogsRequest(this.client, this.gson, this.id));
    }

    /**
     * Get the server RAM
     *
     * @return ram info
     * @throws IOException connection errors
     */
    public CompletableFuture<ServerRAMInfo> getRAM() throws IOException {
        return client.request(new GetServerRAMRequest(this.client, this.gson, this.id));
    }

    /**
     * Set the sever RAM
     *
     * @param ram new RAM in GB
     * @return new ram info
     * @throws IOException connection errors
     */
    public CompletableFuture<ServerRAMInfo> setRAM(int ram) throws IOException {
        return client.request(new SetServerRAMRequest(this.client, this.gson, this.id, ram));
    }

    /**
     * Start the server. Equivalent to {@link #start(boolean)} with useOwnCredits = false.
     *
     * @return Completable future with an updated server object. This future completes after the request, not once the server has started.
     * @throws IOException connection errors
     */
    public CompletableFuture<Void> start() throws IOException {
        return this.start(false);
    }

    /**
     * Start the server
     *
     * @param useOwnCredits use the credits of the account that created the API key instead of the server owner's credits
     * @return Completable future with an updated server object. This future completes after the request, not once the server has started.
     * @throws IOException connection errors
     */
    public CompletableFuture<Void> start(boolean useOwnCredits) throws IOException {
        return client.request(new StartServerRequest(this.client, this.gson, this.id, useOwnCredits));
    }

    /**
     * Stop the server
     *
     * @return Completable future with an updated server object. This future completes after the request, not once the server has stopped.
     * @throws IOException connection errors
     */
    public CompletableFuture<Void> stop() throws IOException {
        return client.request(new StopServerRequest(this.client, this.gson, this.id));
    }

    /**
     * Restart the server
     *
     * @return Completable future with an updated server object. This future completes after the request, not once the server has restarted.
     * @throws IOException connection errors
     */
    public CompletableFuture<Void> restart() throws IOException {
        return client.request(new RestartServerRequest(this.client, this.gson, this.id));
    }

    /**
     * Wait until the server has reached a certain status. It is highly recommended to attach a timeout to the future
     * returned by this method and/or adding the crashed status to the set of statuses to prevent the future from
     * hanging indefinitely if the server fails to start/stop.
     *
     * @param statuses the statuses to wait for
     * @return a future that completes when the server has reached one of the given statuses
     */
    public Future<Server> waitForStatus(@NotNull Set<ServerStatus> statuses) {
        return this.subscribe().waitForStatus(Objects.requireNonNull(statuses));
    }

    /**
     * Wait until the server has reached a certain status. It is highly recommended to attach a timeout to the future
     * returned by this method and/or adding the crashed status to the set of statuses to prevent the future from
     * hanging indefinitely if the server fails to start/stop.
     *
     * @param statuses the statuses to wait for
     * @return a future that completes when the server has reached one of the given statuses
     */
    public Future<Server> waitForStatus(@NotNull ServerStatus... statuses) {
        return waitForStatus(Set.of(Objects.requireNonNull(statuses)));
    }

    /**
     * Execute a server command. If a websocket connection with the console stream is active the command will be sent via the websocket.
     *
     * @param command command that will be sent to the console
     * @return completable future that completes once the command has been sent
     * @throws IOException connection errors
     */
    public CompletableFuture<Void> executeCommand(@NotNull String command) throws IOException {
        Objects.requireNonNull(command);

        if (this.webSocket != null) {
            return this.webSocket.executeCommand(command);
        }

        return client.request(new ExecuteCommandRequest(this.client, this.gson, this.id, command));
    }

    /**
     * Extend the server stop time by the given amount of minutes. This only works if the server is currently stopping.
     * @param time time in minutes to extend the stop time by
     * @return completable future that completes once the request has been sent
     * @throws IOException connection errors
     */
    @ApiStatus.AvailableSince("2.3.0")
    public CompletableFuture<Void> extendStopTime(int time) throws IOException {
        return client.request(new ExtendServerStopTimeRequest(this.client, this.gson, this.id, time));
    }

    /**
     * Get a list of available player lists
     *
     * @return available player lists
     * @throws IOException connection errors
     */
    public CompletableFuture<List<String>> getPlayerLists() throws IOException {
        return client.request(new GetPlayerListsRequest(this.client, this.gson, this.id));
    }

    /**
     * Get a file. This method does not request any data of the file.
     *
     * @param path file path
     * @return empty ServerFile object
     * @see ServerFile#fetch()
     */
    public ServerFile getFile(@NotNull String path) {
        return new ServerFile(this.client, this.gson, this, path);
    }

    /**
     * Get a player list.
     *
     * @param name player list name (see {@link #getPlayerLists()})
     * @return player list
     */
    public PlayerList getPlayerList(@NotNull String name) {
        return new PlayerList(this.client, this.gson, this.id, name);
    }

    /**
     * Subscribe to server status changes
     *
     * @param subscriber status change handler
     */
    public void addStatusSubscriber(@NotNull ServerStatusSubscriber subscriber) {
        this.subscribe().addStreamSubscriber(ServerStatusStream.class, subscriber);
    }

    /**
     * Unsubscribe from server status changes
     *
     * @param subscriber status change handler
     */
    public void removeStatusSubscriber(@NotNull ServerStatusSubscriber subscriber) {
        if (this.webSocket == null) {
            return;
        }

        this.subscribe().removeStreamSubscriber(ServerStatusStream.class, subscriber);
    }

    /**
     * Subscribe to console messages
     *
     * @param subscriber console message handler
     */
    public void addConsoleSubscriber(@NotNull ConsoleSubscriber subscriber) {
        this.subscribe().addStreamSubscriber(ConsoleStream.class, subscriber);
    }

    /**
     * Unsubscribe from console messages
     *
     * @param subscriber console message handler
     */
    public void removeConsoleSubscriber(@NotNull ConsoleSubscriber subscriber) {
        if (this.webSocket == null) {
            return;
        }

        this.subscribe().removeStreamSubscriber(ConsoleStream.class, subscriber);
    }

    /**
     * Subscribe to heap data
     *
     * @param subscriber heap data handler
     */
    public void addHeapSubscriber(@NotNull HeapSubscriber subscriber) {
        this.subscribe().addStreamSubscriber(HeapStream.class, subscriber);
    }

    /**
     * Unsubscribe from heap data
     *
     * @param subscriber heap data handler
     */
    public void removeHeapSubscriber(@NotNull HeapSubscriber subscriber) {
        if (this.webSocket == null) {
            return;
        }

        this.subscribe().removeStreamSubscriber(HeapStream.class, subscriber);
    }

    /**
     * Subscribe to stats
     *
     * @param subscriber stats handler
     */
    public void addStatsSubscriber(@NotNull StatsSubscriber subscriber) {
        this.subscribe().addStreamSubscriber(StatsStream.class, subscriber);
    }

    /**
     * Unsubscribe from stats
     *
     * @param subscriber stats handler
     */
    public void removeStatsSubscriber(@NotNull StatsSubscriber subscriber) {
        if (this.webSocket == null) {
            return;
        }

        this.subscribe().removeStreamSubscriber(StatsStream.class, subscriber);
    }

    /**
     * Subscribe to tick events
     *
     * @param subscriber tick data handler
     */
    public void addTickSubscriber(@NotNull TickSubscriber subscriber) {
        this.subscribe().addStreamSubscriber(TickStream.class, subscriber);
    }

    /**
     * Unsubscribe from tick events
     *
     * @param subscriber tick data handler
     */
    public void removeTickSubscriber(@NotNull TickSubscriber subscriber) {
        if (this.webSocket == null) {
            return;
        }

        this.subscribe().removeStreamSubscriber(TickStream.class, subscriber);
    }

    /**
     * Subscribe to server management notifications
     * @param subscriber management notification handler
     */
    @ApiStatus.AvailableSince("2.4.0")
    public void addServerManagementNotificationSubscriber(@NotNull ManagementNotificationSubscriber subscriber) {
        this.subscribe().addStreamSubscriber(ServerManagementStream.class, subscriber);
    }

    /**
     * Unsubscribe from server management notifications
     * @param subscriber management notification handler
     */
    @ApiStatus.AvailableSince("2.4.0")
    public void removeServerManagementNotificationSubscriber(@NotNull ManagementNotificationSubscriber subscriber) {
        if (this.webSocket == null) {
            return;
        }

        this.subscribe().removeStreamSubscriber(ServerManagementStream.class, subscriber);
    }

    /**
     * Send a server management request. This requires the management server to be online. It also automatically
     * subscribes to the management stream if not already done. This means you have to manually unsubscribe using
     * {@link WebSocketConnection#unsubscribe(StreamType)}} if you want to close the connection.
     * @param method name of the method to call
     * @param params arguments for the method (can be null)
     * @return future that completes with the result of the request
     */
    @ApiStatus.AvailableSince("2.4.0")
    public CompletableFuture<JsonElement> sendServerManagementRequest(@NotNull String method, @Nullable JsonElement params) {
        return this.subscribe().getOrCreateStream(ServerManagementStream.class).sendRequest(method, params);
    }

    /**
     * Get the current WebSocketConnection. A new connection will be created automatically if a subscriber is added.
     * @return web socket connection or null
     */
    public Optional<WebSocketConnection> getWebSocket() {
        return Optional.ofNullable(webSocket);
    }

    /**
     * Unsubscribe from websocket events. This happens automatically when there are no registered subscribers, but you
     * might still want to call this method explicitly as a cleanup step. If no connection is active, this method does
     * nothing.
     */
    public void unsubscribe() {
        if (this.webSocket == null) {
            return;
        }

        this.webSocket.close();
        this.webSocket = null;
    }

    /**
     * update properties from fetched object
     *
     * @param server server fetched from the API
     * @return updated server object
     */
    @ApiStatus.Internal
    public Server setFromObject(Server server) {
        this.fetched = true;
        this.id = server.getId();
        this.name = server.getName();
        this.address = server.getAddress();
        this.motd = server.getMotd();
        this.status = server.getStatus().getValue();
        this.players = server.getPlayerInfo();
        this.host = server.getHost().orElse(null);
        this.port = server.getPort().orElse(null);
        this.software = server.getSoftware();
        this.shared = server.isShared();
        this.permissions = server.getPermissions().orElse(null);
        this.region = server.getRegion();
        return this;
    }

    /**
     * Set the exaroton client used for requests and the gson instance. This method assumes the server is already fetched.
     *
     * @param client exaroton client used for new requests
     * @param gson   gson instance used for (de-)serialization
     */
    @ApiStatus.Internal
    @Override
    public void initialize(@NotNull ExarotonClient client, @NotNull Gson gson) {
        this.client = Objects.requireNonNull(client);
        this.gson = Objects.requireNonNull(gson);
        this.fetched = true;
    }

    /**
     * Subscribe to websocket events.
     */
    private WebSocketConnection subscribe() {
        if (this.webSocket != null) {
            return this.webSocket;
        }

        return this.webSocket = client.connectToWebSocket(this, "servers/" + this.id + "/websocket");
    }
}
