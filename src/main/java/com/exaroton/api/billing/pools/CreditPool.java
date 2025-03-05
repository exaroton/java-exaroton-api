package com.exaroton.api.billing.pools;

import com.exaroton.api.APIException;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.request.billing.pools.GetCreditPoolMembersRequest;
import com.exaroton.api.request.billing.pools.GetCreditPoolRequest;
import com.exaroton.api.request.billing.pools.GetCreditPoolServersRequest;
import com.exaroton.api.server.Server;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CreditPool {
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
    private @NotNull transient ExarotonClient client;

    /**
     * Gson instance used for (de-)serialization
     */
    private @NotNull transient Gson gson;

    /**
     * Create a new Credit Pool
     * @param client exaroton client
     * @param gson gson instance
     * @param id unique pool id
     */
    public CreditPool(@NotNull ExarotonClient client, @NotNull Gson gson, @NotNull String id) {
        this.client = Objects.requireNonNull(client);
        this.gson = Objects.requireNonNull(gson);
        this.id = Objects.requireNonNull(id);
    }

    /**
     * Get the unique id of the pool
     * @return unique pool id
     */
    public @NotNull String getId() {
        return id;
    }

    /**
     * Get the display name of the pool
     * @return pool display name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the current amount of credits in the pool
     * @return amount of credits in the pool
     */
    public double getCredits() {
        return credits;
    }

    /**
     * Get the number of servers in the pool
     * @return number of servers in the pool
     */
    public int getServers() {
        return servers;
    }

    /**
     * Get the id of the user that owns the pool
     * @return pool owner id
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Is the current user the owner of the pool
     * @return is the current user the owner of the pool
     */
    public boolean isOwner() {
        return isOwner;
    }

    /**
     * Get the number of members in the pool
     * @return number of members in the pool
     */
    public int getMembers() {
        return members;
    }

    /**
     * Get the share of credits in the pool that belong to the current user
     * @return share of credits in the pool that belong to the current user
     */
    public double getOwnShare() {
        return ownShare;
    }

    /**
     * Get the amount of credits in the pool that belong to the current user
     * @return amount of credits in the pool that belong to the current user
     */
    public double getOwnCredits() {
        return ownCredits;
    }

    /**
     * Set the exaroton client used for further requests
     * @param client exaroton client
     * @return updated pool object
     */
    public CreditPool setClient(ExarotonClient client) {
        this.client = client;
        return this;
    }

    /**
     * Mark this pool as fetched from the API
     * @return updated pool object
     */
    public CreditPool setFetched() {
        this.fetched = true;
        return this;
    }

    /**
     * Fetch the Credit Pool from the API
     * @return full credit pool
     * @throws APIException connection or API errors
     */
    public CreditPool get() throws APIException {
        this.fetched = true;
        GetCreditPoolRequest request = new GetCreditPoolRequest(this.client, this.gson, this.id);
        return this.setFromObject(request.request().getData());
    }

    /**
     * Fetch the Credit Pool from the API if it hasn't been fetched yet
     * @return full credit pool
     * @throws APIException connection or API errors
     */
    public CreditPool getIfNotFetched() throws APIException {
        if (!this.fetched) {
            return this.get();
        }
        return this;
    }

    /**
     * Get a list of members in this pool
     * @return array of pool members
     * @throws APIException connection or API errors
     */
    public CreditPoolMember[] getMemberList() throws APIException {
        GetCreditPoolMembersRequest request = new GetCreditPoolMembersRequest(this.client, this.gson, this.id);
        return request.request().getData();
    }

    /**
     * Get a list of servers in this pool
     * @return array of pool servers
     * @throws APIException connection or API errors
     */
    public Server[] getServerList() throws APIException {
        GetCreditPoolServersRequest request = new GetCreditPoolServersRequest(this.client, this.gson, this.id);
        Server[] servers = request.request().getData();
        for (Server server: servers) {
            server.init(this.client, gson);
            server.fetched = true;
        }
        return servers;
    }

    /**
     * update properties from fetched object
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
