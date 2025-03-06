package com.exaroton.api.billing.pools;

@SuppressWarnings("unused")
public class CreditPoolMember {
    /**
     * Unique ID of the account
     */
    private String account;
    /**
     * Unique (but changeable) display name of the account.
     */
    private String name;
    /**
     * The share of credits in the pool that belong to the account.
     * e.g. 0.5 means the account owns 50% of the credits in the pool.
     */
    private double share;
    /**
     * The amount of credits in the pool that belong to the account.
     */
    private double credits;
    /**
     * Is the account the owner of the pool
     */
    private boolean isOwner;

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
