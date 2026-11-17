package com.exaroton.api.server.domains;

import com.exaroton.api.ExarotonClient;
import com.exaroton.api.request.server.domains.*;
import com.exaroton.api.util.Initializable;
import com.exaroton.api.util.ParameterValidator;
import com.google.gson.Gson;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * A custom domain of a server. To link the domain, set its NS records at your DNS provider to the
 * {@link #getNameservers() nameservers}. All domain methods require the domains permission (only relevant for
 * servers accessed via Share Access).
 *
 * @see com.exaroton.api.server.Server#getDomains()
 * @see com.exaroton.api.server.Server#getDomain(String)
 */
@SuppressWarnings("unused")
@ApiStatus.AvailableSince("2.6.0")
public final class Domain implements Initializable {
    /**
     * Has this domain been fetched from the API yet
     */
    private transient boolean fetched;

    /**
     * Unique ID of the server this domain belongs to
     */
    private transient String serverId;

    /**
     * Unique custom domain ID
     */
    private String id;

    /**
     * The domain (lowercase)
     */
    private String domain;

    /**
     * Status of the domain
     */
    private DomainStatus status;

    /**
     * Protocol of the domain
     */
    private DomainProtocol protocol;

    /**
     * Port of the domain
     */
    private int port;

    /**
     * Unix timestamp of the last check
     */
    @Nullable
    private Long checkedTime;

    /**
     * Unix timestamp of when the domain was added
     */
    @Nullable
    private Long createdTime;

    /**
     * The nameservers the NS records of the domain have to point to
     */
    private List<String> nameservers = List.of();

    /**
     * The client used to create this domain object
     */
    private transient ExarotonClient client;

    /**
     * gson instance
     */
    private transient Gson gson;

    /**
     * Use {@link com.exaroton.api.server.Server#getDomain(String)} instead of calling this constructor directly.
     *
     * @param client   exaroton client
     * @param gson     gson instance
     * @param serverId server ID
     * @param id       domain ID
     */
    @ApiStatus.Internal
    public Domain(@NotNull ExarotonClient client, @NotNull Gson gson, @NotNull String serverId, @NotNull String id) {
        this.client = Objects.requireNonNull(client);
        this.gson = Objects.requireNonNull(gson);
        this.serverId = ParameterValidator.requireValidId(serverId);
        this.id = ParameterValidator.requireNonEmpty(id, "id");
    }

    /**
     * @return unique custom domain ID
     */
    public String getId() {
        return id;
    }

    /**
     * Has this domain been fetched from the API yet. If this is false only the ID and server ID are known.
     *
     * @return has this domain been fetched from the API yet
     */
    public boolean isFetched() {
        return fetched;
    }

    /**
     * @return unique ID of the server this domain belongs to
     */
    public String getServerId() {
        return serverId;
    }

    /**
     * @return the domain in lowercase (e.g. play.example.com)
     */
    public String getDomain() {
        return domain;
    }

    /**
     * @return status of the domain
     */
    public DomainStatus getStatus() {
        return status;
    }

    /**
     * @return protocol of the domain
     */
    public DomainProtocol getProtocol() {
        return protocol;
    }

    /**
     * @return port of the domain
     */
    public int getPort() {
        return port;
    }

    /**
     * @return time of the last check
     */
    public Optional<Instant> getCheckedTime() {
        return Optional.ofNullable(checkedTime).map(Instant::ofEpochSecond);
    }

    /**
     * @return time the domain was added
     */
    public Optional<Instant> getCreatedTime() {
        return Optional.ofNullable(createdTime).map(Instant::ofEpochSecond);
    }

    /**
     * @return the nameservers the NS records of the domain have to point to
     */
    public List<String> getNameservers() {
        return nameservers == null ? List.of() : nameservers;
    }

    /**
     * Fetch the domain from the API
     *
     * @return the domain with updated properties
     * @throws IOException connection errors
     */
    public CompletableFuture<Domain> fetch() throws IOException {
        return client.request(new GetDomainRequest(client, gson, serverId, id))
                .thenApply(this::setFromObject);
    }

    /**
     * Change the protocol and/or port of the domain. Values that are null are not changed, but at least one has to
     * be set.
     *
     * @param protocol new protocol or null
     * @param port     new port (1 - 65535) or null
     * @return the domain with updated properties
     * @throws IOException connection errors
     */
    public CompletableFuture<Domain> update(@Nullable DomainProtocol protocol, @Nullable Integer port)
            throws IOException {
        return client.request(new UpdateDomainRequest(client, gson, serverId, id, protocol, port))
                .thenApply(this::setFromObject);
    }

    /**
     * Change the port of the domain
     *
     * @param port new port (1 - 65535)
     * @return the domain with updated properties
     * @throws IOException connection errors
     */
    public CompletableFuture<Domain> setPort(int port) throws IOException {
        return update(null, port);
    }

    /**
     * Change the protocol of the domain
     *
     * @param protocol new protocol
     * @return the domain with updated properties
     * @throws IOException connection errors
     */
    public CompletableFuture<Domain> setProtocol(@NotNull DomainProtocol protocol) throws IOException {
        return update(Objects.requireNonNull(protocol), null);
    }

    /**
     * Check again whether the NS records of the domain point to the {@link #getNameservers() nameservers} and update
     * its status. A domain is checked at most every 10 seconds, more frequent requests return the domain unchanged.
     * Linked domains are not checked again.
     *
     * @return the domain with updated properties
     * @throws IOException connection errors
     */
    public CompletableFuture<Domain> check() throws IOException {
        return client.request(new CheckDomainRequest(client, gson, serverId, id))
                .thenApply(this::setFromObject);
    }

    /**
     * Remove the domain from the server
     *
     * @return completable future that completes once the domain has been removed
     * @throws IOException connection errors
     */
    public CompletableFuture<Void> delete() throws IOException {
        return client.request(new DeleteDomainRequest(client, gson, serverId, id));
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
     * Set the ID of the server this domain belongs to. The API doesn't include it in the response.
     *
     * @param serverId server ID
     * @return this
     */
    @ApiStatus.Internal
    public Domain setServerId(@NotNull String serverId) {
        this.serverId = Objects.requireNonNull(serverId);
        return this;
    }

    /**
     * update properties from fetched object
     *
     * @param domain domain fetched from the API
     * @return updated domain object
     */
    private Domain setFromObject(Domain domain) {
        this.fetched = true;
        this.id = domain.id;
        this.domain = domain.domain;
        this.status = domain.status;
        this.protocol = domain.protocol;
        this.port = domain.port;
        this.checkedTime = domain.checkedTime;
        this.createdTime = domain.createdTime;
        this.nameservers = domain.nameservers;
        return this;
    }
}
