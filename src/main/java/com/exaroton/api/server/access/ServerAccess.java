package com.exaroton.api.server.access;

import com.exaroton.api.ExarotonClient;
import com.exaroton.api.request.server.access.*;
import com.exaroton.api.util.Initializable;
import com.exaroton.api.util.ParameterValidator;
import com.google.gson.Gson;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * The access of an account to a server via the Share Access feature. All access methods require the access
 * permission (only relevant for servers accessed via Share Access).
 * <p>
 * Available permissions: server, stop, console, log, options, reinstall, domains, software, players, files, addons,
 * worlds, backups, transactions, duplicate and access. Some permissions require others: stop requires server;
 * reinstall and domains require options; access requires all other permissions.
 *
 * @see com.exaroton.api.server.Server#getAccessList()
 * @see com.exaroton.api.server.Server#getAccess(String)
 */
@SuppressWarnings("unused")
@ApiStatus.AvailableSince("2.6.0")
public final class ServerAccess implements Initializable {
    /**
     * Has this access been fetched from the API yet
     */
    private transient boolean fetched;

    /**
     * Unique ID of the server
     */
    private transient String serverId;

    /**
     * Unique ID of the account's access to the server
     */
    private String id;

    /**
     * The account the server is shared with
     */
    private AccessAccount account;

    /**
     * Permissions of the account on the server
     */
    private List<String> permissions = List.of();

    /**
     * The client used to create this object
     */
    private transient ExarotonClient client;

    /**
     * gson instance
     */
    private transient Gson gson;

    /**
     * Use {@link com.exaroton.api.server.Server#getAccess(String)} instead of calling this constructor directly.
     *
     * @param client   exaroton client
     * @param gson     gson instance
     * @param serverId server ID
     * @param id       access ID
     */
    @ApiStatus.Internal
    public ServerAccess(
            @NotNull ExarotonClient client,
            @NotNull Gson gson,
            @NotNull String serverId,
            @NotNull String id
    ) {
        this.client = Objects.requireNonNull(client);
        this.gson = Objects.requireNonNull(gson);
        this.serverId = ParameterValidator.requireValidId(serverId);
        this.id = ParameterValidator.requireNonEmpty(id, "id");
    }

    /**
     * @return unique ID of the account's access to the server
     */
    public String getId() {
        return id;
    }

    /**
     * Has this access been fetched from the API yet. If this is false only the ID and server ID are known.
     *
     * @return has this access been fetched from the API yet
     */
    public boolean isFetched() {
        return fetched;
    }

    /**
     * @return unique ID of the server
     */
    public String getServerId() {
        return serverId;
    }

    /**
     * @return the account the server is shared with
     */
    public AccessAccount getAccount() {
        return account;
    }

    /**
     * @return permissions of the account on the server (e.g. server, stop)
     */
    public List<String> getPermissions() {
        return permissions == null ? List.of() : permissions;
    }

    /**
     * Fetch the access from the API
     *
     * @return the access with updated properties
     * @throws IOException connection errors
     */
    public CompletableFuture<ServerAccess> fetch() throws IOException {
        return client.request(new GetAccessRequest(client, gson, serverId, id))
                .thenApply(this::setFromObject);
    }

    /**
     * Replace the permissions of the account on the server. An account that is currently running the server with
     * its own credits keeps the permissions server and stop until the server is offline, and an account that has a
     * backup account linked to the server keeps the permission backups.
     *
     * @param permissions new permissions
     * @return the access with updated properties
     * @throws IOException connection errors
     */
    public CompletableFuture<ServerAccess> setPermissions(@NotNull Collection<String> permissions) throws IOException {
        return client.request(new UpdateAccessRequest(client, gson, serverId, id, permissions))
                .thenApply(this::setFromObject);
    }

    /**
     * Replace the permissions of the account on the server
     *
     * @param permissions new permissions
     * @return the access with updated properties
     * @throws IOException connection errors
     * @see #setPermissions(Collection)
     */
    public CompletableFuture<ServerAccess> setPermissions(@NotNull String... permissions) throws IOException {
        return setPermissions(List.of(permissions));
    }

    /**
     * Remove the access of the account to the server. This fails while the account is running the server with its
     * own credits or has a backup account linked to the server.
     *
     * @return completable future that completes once the access has been removed
     * @throws IOException connection errors
     */
    public CompletableFuture<Void> delete() throws IOException {
        return client.request(new DeleteAccessRequest(client, gson, serverId, id));
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
     * Set the ID of the server. The API doesn't include it in the response.
     *
     * @param serverId server ID
     * @return this
     */
    @ApiStatus.Internal
    public ServerAccess setServerId(@NotNull String serverId) {
        this.serverId = Objects.requireNonNull(serverId);
        return this;
    }

    /**
     * update properties from fetched object
     *
     * @param access access fetched from the API
     * @return updated access object
     */
    private ServerAccess setFromObject(ServerAccess access) {
        this.fetched = true;
        this.id = access.id;
        this.account = access.account;
        this.permissions = access.permissions;
        return this;
    }
}
