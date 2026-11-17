package com.exaroton.api.server.domains;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.ApiStatus;

/**
 * Protocol of a custom domain
 */
@ApiStatus.AvailableSince("2.6.0")
public enum DomainProtocol {
    @SerializedName("https")
    HTTPS,
}
