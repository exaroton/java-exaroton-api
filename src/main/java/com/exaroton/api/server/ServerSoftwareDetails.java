package com.exaroton.api.server;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Details about installing a specific software on a specific server
 */
@SuppressWarnings("unused")
@ApiStatus.AvailableSince("2.5.0")
public final class ServerSoftwareDetails extends ServerSoftware {
    /**
     * Description of the software
     */
    @Nullable
    private String description;

    /**
     * Link with more information about the software
     */
    @Nullable
    private String link;

    /**
     * Whether installing this software on the server requires a reinstall
     */
    private boolean reinstall;

    /**
     * @return description of the software
     */
    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    /**
     * @return link with more information about the software
     */
    public Optional<String> getLink() {
        return Optional.ofNullable(link);
    }

    /**
     * Whether installing this software on the server requires a reinstall. A reinstall deletes all server files.
     *
     * @return true if installing this software requires a reinstall
     * @see Server#installSoftware(String, boolean)
     */
    public boolean requiresReinstall() {
        return reinstall;
    }
}
