package com.exaroton.api.server;

import com.exaroton.api.software.Software;
import com.exaroton.api.util.ParameterValidator;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.ZoneId;
import java.util.Objects;

/**
 * Options for creating a new server. Use {@link #withSoftware(String, String)} or
 * {@link #withSoftwareType(String, String)} to create an instance.
 *
 * @see com.exaroton.api.ExarotonClient#createServer(CreateServerOptions)
 */
@SuppressWarnings("unused")
@ApiStatus.AvailableSince("2.5.0")
public final class CreateServerOptions {
    /**
     * Preferred server name
     */
    private final String name;

    /**
     * ID of the software to install
     */
    @Nullable
    private final String software;

    /**
     * Software type to install the latest version of
     */
    @Nullable
    private final String softwareType;

    /**
     * Server MOTD
     */
    @Nullable
    private String motd;

    /**
     * IANA time zone identifier
     */
    @Nullable
    private String timezone;

    /**
     * Server region
     */
    @Nullable
    private ServerRegion region;

    private CreateServerOptions(@NotNull String name, @Nullable String software, @Nullable String softwareType) {
        this.name = ParameterValidator.requireNonEmpty(name, "name");
        this.software = software;
        this.softwareType = softwareType;
    }

    /**
     * Create options for a server with a specific software
     *
     * @param name       preferred name of the server. If this name is taken, a random name based on it will be generated.
     * @param softwareId id of the software to install (see {@link com.exaroton.api.ExarotonClient#getSoftwareList()})
     * @return new server options
     */
    public static CreateServerOptions withSoftware(@NotNull String name, @NotNull String softwareId) {
        return new CreateServerOptions(name, ParameterValidator.requireValidId(softwareId), null);
    }

    /**
     * Create options for a server with a specific software
     *
     * @param name     preferred name of the server. If this name is taken, a random name based on it will be generated.
     * @param software software to install (see {@link com.exaroton.api.ExarotonClient#getSoftwareList()})
     * @return new server options
     */
    public static CreateServerOptions withSoftware(@NotNull String name, @NotNull Software software) {
        return withSoftware(name, Objects.requireNonNull(software, "software").getId());
    }

    /**
     * Create options for a server with the latest version of a software type
     *
     * @param name         preferred name of the server. If this name is taken, a random name based on it will be
     *                     generated.
     * @param softwareType software type (e.g. vanilla)
     * @return new server options
     */
    public static CreateServerOptions withSoftwareType(@NotNull String name, @NotNull String softwareType) {
        return new CreateServerOptions(name, null, ParameterValidator.requireNonEmpty(softwareType, "softwareType"));
    }

    /**
     * Set the MOTD of the server. If not set, a default MOTD will be generated.
     *
     * @param motd server MOTD
     * @return this
     */
    public CreateServerOptions setMotd(@Nullable String motd) {
        this.motd = motd;
        return this;
    }

    /**
     * Set the timezone of the server. Defaults to UTC.
     *
     * @param timezone IANA time zone identifier (e.g. Europe/Berlin)
     * @return this
     */
    public CreateServerOptions setTimezone(@Nullable String timezone) {
        this.timezone = timezone;
        return this;
    }

    /**
     * Set the timezone of the server. Defaults to UTC.
     *
     * @param timezone time zone
     * @return this
     */
    public CreateServerOptions setTimezone(@Nullable ZoneId timezone) {
        return setTimezone(timezone == null ? null : timezone.getId());
    }

    /**
     * Set the region to create the server in. If not set, the region is chosen based on the timezone.
     *
     * @param region server region
     * @return this
     */
    public CreateServerOptions setRegion(@Nullable ServerRegion region) {
        this.region = region;
        return this;
    }

    /**
     * @return preferred server name
     */
    public String getName() {
        return name;
    }

    /**
     * @return ID of the software to install
     */
    public @Nullable String getSoftware() {
        return software;
    }

    /**
     * @return software type to install the latest version of
     */
    public @Nullable String getSoftwareType() {
        return softwareType;
    }

    /**
     * @return server MOTD
     */
    public @Nullable String getMotd() {
        return motd;
    }

    /**
     * @return IANA time zone identifier
     */
    public @Nullable String getTimezone() {
        return timezone;
    }

    /**
     * @return server region
     */
    public @Nullable ServerRegion getRegion() {
        return region;
    }
}
