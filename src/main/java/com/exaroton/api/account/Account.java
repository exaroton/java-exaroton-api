package com.exaroton.api.account;

import org.jetbrains.annotations.ApiStatus;

public class Account {

    /**
     * Username
     */
    public final String name;

    /**
     * email address
     */
    public final String email;

    /**
     * is the email address verified
     */
    public final boolean verified;

    /**
     * credit count
     */
    private final double credits;

    @ApiStatus.Internal
    public Account(String name, String email, boolean verified, int credits) {
        this.name = name;
        this.email = email;
        this.verified = verified;
        this.credits = credits;
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
}
