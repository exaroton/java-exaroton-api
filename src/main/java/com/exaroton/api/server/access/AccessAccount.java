package com.exaroton.api.server.access;

import org.jetbrains.annotations.ApiStatus;

/**
 * An account a server is shared with
 *
 * @see ServerAccess#getAccount()
 */
@SuppressWarnings("unused")
@ApiStatus.AvailableSince("2.6.0")
public final class AccessAccount {
    /**
     * Unique account ID
     */
    private String id;

    /**
     * Account name
     */
    private String name;

    /**
     * @return unique account ID
     */
    public String getId() {
        return id;
    }

    /**
     * @return account name
     */
    public String getName() {
        return name;
    }
}
