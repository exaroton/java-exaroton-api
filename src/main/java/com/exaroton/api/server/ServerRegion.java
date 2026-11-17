package com.exaroton.api.server;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Region a server can be created in
 *
 * @see com.exaroton.api.ExarotonClient#getRegions()
 */
@ApiStatus.AvailableSince("2.5.0")
public enum ServerRegion {
    @SerializedName("eu")
    EU("eu"),
    @SerializedName("na")
    NA("na"),
    ;

    private final String id;

    ServerRegion(String id) {
        this.id = id;
    }

    /**
     * @return region ID used by the API (e.g. eu)
     */
    @ApiStatus.AvailableSince("2.6.0")
    public @NotNull String getId() {
        return id;
    }

    /**
     * Get a region by its ID
     *
     * @param id region ID (e.g. eu)
     * @return the region or empty if no region with this ID is known
     */
    @ApiStatus.AvailableSince("2.6.0")
    public static Optional<ServerRegion> fromId(@Nullable String id) {
        for (ServerRegion region : values()) {
            if (region.id.equals(id)) {
                return Optional.of(region);
            }
        }
        return Optional.empty();
    }
}
