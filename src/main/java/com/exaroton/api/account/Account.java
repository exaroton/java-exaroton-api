package com.exaroton.api.account;

@SuppressWarnings("unused")
public class Account {

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
