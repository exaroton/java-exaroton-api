package com.exaroton.api.server.domains;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.ApiStatus;

/**
 * Status of a custom domain
 */
@ApiStatus.AvailableSince("2.6.0")
public enum DomainStatus {
    /**
     * The domain has not been checked successfully yet
     */
    @SerializedName("pending")
    PENDING,
    /**
     * The NS records of the domain pointed to the {@link Domain#getNameservers() nameservers} on the last check
     */
    @SerializedName("linked")
    LINKED,
    /**
     * The NS records of the domain didn't point to the {@link Domain#getNameservers() nameservers} on the last check
     */
    @SerializedName("unlinked")
    UNLINKED,
}
