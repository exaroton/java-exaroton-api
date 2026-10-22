package com.exaroton.api.account;

import org.jetbrains.annotations.ApiStatus;

@SuppressWarnings("unused")
@ApiStatus.NonExtendable
public class Account {

    /**
     * Unique account ID
     */
    private String id;

    /**
     * Username
     */
    private String name;

    /**
     * email address
     */
    private String email;

    /**
     * is the email address verified
     */
    private boolean verified;

    /**
     * credit count
     */
    private double credits;

    /**
     * Maximum number of servers this account can own
     */
    private int maxServers;

    @ApiStatus.AvailableSince("2.5.0")
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public boolean getVerified() {
        return verified;
    }

    public double getCredits() {
        return credits;
    }

    @ApiStatus.AvailableSince("2.5.0")
    public int getMaxServers() {
        return maxServers;
    }
}
