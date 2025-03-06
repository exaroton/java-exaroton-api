package com.exaroton.api.billing.pools;

import org.jetbrains.annotations.ApiStatus;

public class CreditPoolMember {
    /**
     * Unique ID of the account
     */
    private final String account;
    /**
     * Unique (but changeable) display name of the account.
     */
    private final String name;
    /**
     * The share of credits in the pool that belong to the account.
     * e.g. 0.5 means the account owns 50% of the credits in the pool.
     */
    private final double share;
    /**
     * The amount of credits in the pool that belong to the account.
     */
    private final double credits;
    /**
     * Is the account the owner of the pool
     */
    private final boolean isOwner;

    @ApiStatus.Internal
    public CreditPoolMember(String account, String name, double share, double credits, boolean isOwner) {
        this.account = account;
        this.name = name;
        this.share = share;
        this.credits = credits;
        this.isOwner = isOwner;
    }

    /**
     * @return unique ID of the account
     */
    public String getAccount() {
        return this.account;
    }

    /**
     * @return unique (but changeable) display name of the account
     */
    public String getName() {
        return name;
    }

    /**
     * Get the share of credits in the pool that belong to the account.
     * e.g. 0.5 means the account owns 50% of the credits in the pool.
     *
     * @return the share of credits in the pool that belong to the account.
     */
    public double getShare() {
        return share;
    }

    /**
     * @return the amount of credits in the pool that belong to the account
     */
    public double getCredits() {
        return credits;
    }

    /**
     * @return is the account the owner of the pool
     */
    public boolean isOwner() {
        return isOwner;
    }
}
