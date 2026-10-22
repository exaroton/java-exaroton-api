package com.exaroton.api.server;

import com.exaroton.api.software.Software;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * Software installed on a specific server
 */
@SuppressWarnings("unused")
public class ServerSoftware extends Software {
    /**
     * Addon types (e.g. mod, plugin) supported by this software on this server
     */
    private List<String> addons;

    /**
     * @return addon types (e.g. mod, plugin) supported by this software on this server
     */
    @ApiStatus.AvailableSince("2.5.0")
    public List<String> getAddons() {
        return addons == null ? List.of() : addons;
    }

    /**
     * Check if this software supports a type of addon on this server
     *
     * @param addonType addon type (e.g. mod, plugin)
     * @return true if the addon type is supported
     */
    @ApiStatus.AvailableSince("2.5.0")
    public boolean supportsAddon(@NotNull String addonType) {
        return getAddons().contains(Objects.requireNonNull(addonType));
    }
}
