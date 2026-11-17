package com.exaroton.api.software;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * A server software that can be installed on a server (e.g. a specific version of Vanilla or Paper). This class was
 * added in 2.5.0 as the parent of {@link com.exaroton.api.server.ServerSoftware}, which already provided
 * {@link #getId()}, {@link #getName()} and {@link #getVersion()} before that.
 */
@SuppressWarnings("unused")
@ApiStatus.NonExtendable
public class Software {
    /**
     * Unique ID of the software version
     */
    private String id;

    /**
     * Software name
     */
    private String name;

    /**
     * Software version
     */
    private String version;

    /**
     * Software type (e.g. vanilla)
     */
    private String type;

    /**
     * Launcher this software belongs to. Only set for modpacks that require a specific launcher.
     */
    @Nullable
    private String launcher;

    /**
     * @return unique ID of the software version
     */
    public String getId() {
        return id;
    }

    /**
     * @return name of the software
     */
    public String getName() {
        return name;
    }

    /**
     * @return version of the software
     */
    public String getVersion() {
        return version;
    }

    /**
     * @return type of the software (e.g. vanilla)
     */
    @ApiStatus.AvailableSince("2.5.0")
    public String getType() {
        return type;
    }

    /**
     * @return launcher this software belongs to. Only set for modpacks that require a specific launcher.
     */
    @ApiStatus.AvailableSince("2.5.0")
    public Optional<String> getLauncher() {
        return Optional.ofNullable(launcher);
    }
}
