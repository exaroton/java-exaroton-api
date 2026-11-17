package com.exaroton.api.server;

import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

/**
 * A region servers can be located in
 *
 * @see com.exaroton.api.ExarotonClient#getRegions()
 */
@SuppressWarnings("unused")
@ApiStatus.AvailableSince("2.6.0")
public final class Region {
    /**
     * Unique region ID
     */
    private String id;

    /**
     * Display name of the region
     */
    private String name;

    /**
     * @return unique region ID (e.g. eu)
     */
    public String getId() {
        return id;
    }

    /**
     * @return display name of the region (e.g. Europe)
     */
    public String getName() {
        return name;
    }

    /**
     * @return the matching {@link ServerRegion} or empty if this region is not known to this version of the library
     */
    public Optional<ServerRegion> getServerRegion() {
        return ServerRegion.fromId(id);
    }
}
