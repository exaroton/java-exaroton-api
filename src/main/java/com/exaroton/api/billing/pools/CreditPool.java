package com.exaroton.api.billing.pools;

import com.exaroton.api.APIException;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.Initializable;
import com.exaroton.api.request.billing.pools.GetCreditPoolMembersRequest;
import com.exaroton.api.request.billing.pools.GetCreditPoolRequest;
import com.exaroton.api.request.billing.pools.GetCreditPoolServersRequest;
import com.exaroton.api.server.Server;
import com.google.gson.Gson;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public final class CreditPool implements Initializable {
    /**
     * Has this pool been fetched from the API yet
     */
    private boolean fetched;

    /**
     * The unique id of the pool
     */
    private final @NotNull String id;

    /**
     * The display name of the pool
     */
    private String name;

    /**
     * The current amount of credits in the pool
     */
    private double credits;

    /**
     * The number of servers in the pool
     */
    private int servers;

    /**
     * The id of the user that owns the pool
     */
    private String owner;

    /**
     * Is the current user the owner of the pool
     */
    private boolean isOwner;

    /**
     * The number of members in the pool
     */
    private int members;

    /**
     * The share of credits in the pool that belong to the current user
     */
    private double ownShare;

    /**
     * The amount of credits in the pool that belong to the current user
     */
    private double ownCredits;

    /**
     * The client used to create this pool
     */
    @NotNull
    private transient ExarotonClient client;

    /**
     * Create a new Credit Pool
     *
     * @param client exaroton client
     * @param id     unique pool id
     */
    @ApiStatus.Internal
    public CreditPool(@NotNull ExarotonClient client, @NotNull String id) {
        this.client = Objects.requireNonNull(client);
        this.id = Objects.requireNonNull(id);
    }

    /**
     * Get the unique id of the pool
     *
     * @return unique pool id
     */
    public @NotNull String getId() {
        return id;
    }

    /**
     * Get the display name of the pool
     *
     * @return pool display name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the current amount of credits in the pool
     *
     * @return amount of credits in the pool
     */
    public double getCredits() {
        return credits;
    }

    /**
     * Get the number of servers in the pool
     *
     * @return number of servers in the pool
     */
    public int getServers() {
        return servers;
    }

    /**
     * Get the id of the user that owns the pool
     *
     * @return pool owner id
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Is the current user the owner of the pool
     *
     * @return is the current user the owner of the pool
     */
    public boolean isOwner() {
        return isOwner;
    }

    /**
     * Get the number of members in the pool
     *
     * @return number of members in the pool
     */
    public int getMembers() {
        return members;
    }

    /**
     * Get the share of credits in the pool that belong to the current user
     *
     * @return share of credits in the pool that belong to the current user
     */
    public double getOwnShare() {
        return ownShare;
    }

    /**
     * Get the amount of credits in the pool that belong to the current user
     *
     * @return amount of credits in the pool that belong to the current user
     */
    public double getOwnCredits() {
        return ownCredits;
    }

    /**
     * Set the exaroton client used for further requests
     *
     * @param client exaroton client
     */
    @ApiStatus.Internal
    @Override
    public void initialize(@NotNull ExarotonClient client, @NotNull Gson gson) {
        this.client = Objects.requireNonNull(client);
        this.fetched = true;
    }

    /**
     * Fetch the Credit Pool from the API
     *
     * @return the credit pool with the fetched data
     * @throws IOException connection errors
     */
    public CompletableFuture<CreditPool> get() throws IOException {
        return client.request(new GetCreditPoolRequest(this.id))
                .thenApply(response -> {
                    this.fetched = true;
                    return this.setFromObject(response);
                });
    }

    /**
     * Fetch the Credit Pool from the API if it hasn't been fetched yet
     *
     * @return full credit pool
     * @throws IOException connection errors
     */
    public CompletableFuture<CreditPool> getIfNotFetched() throws IOException {
        if (!this.fetched) {
            return this.get();
        }
        return CompletableFuture.completedFuture(this);
    }

    /**
     * Get a list of members in this pool
     *
     * @return list of pool members
     * @throws IOException connection errors
     */
    public CompletableFuture<List<CreditPoolMember>> getMemberList() throws IOException {
        return client.request(new GetCreditPoolMembersRequest(this.id));
    }

    /**
     * Get a list of servers in this pool
     *
     * @return list of pool servers
     * @throws IOException connection errors
     */
    public CompletableFuture<List<Server>> getServerList() throws IOException {
        return client.request(new GetCreditPoolServersRequest(this.id));
    }

    /**
     * update properties from fetched object
     *
     * @param pool pool fetched from the API
     * @return updated pool object
     */
    private CreditPool setFromObject(CreditPool pool) {
        this.name = pool.getName();
        this.credits = pool.getCredits();
        this.servers = pool.getServers();
        this.owner = pool.getOwner();
        this.isOwner = pool.isOwner();
        this.members = pool.getMembers();
        this.ownShare = pool.getOwnShare();
        this.ownCredits = pool.getOwnCredits();
        return this;
    }
}
