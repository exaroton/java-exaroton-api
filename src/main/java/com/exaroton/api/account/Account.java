package com.exaroton.api.account;

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
    private final int credits;


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

    public int getCredits() {
        return credits;
    }
}