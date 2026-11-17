package com.exaroton.api.server;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Server properties (e.g. auto stop). Only properties that are available for the server (depending on its software
 * and features) and that your account is allowed to change are returned by the API, so all getters return an empty
 * optional for properties that are not available.
 * <p>
 * To change properties, create a new instance, set the properties you want to change and pass it to
 * {@link Server#setProperties(ServerProperties)}. Properties that are not set are not changed. Changing
 * {@link #setAutoUpdate(AutoUpdateMode) auto-update} requires the software permission, all other properties require
 * the options permission (only relevant for servers accessed via Share Access).
 *
 * @see Server#getProperties()
 */
@SuppressWarnings("unused")
@ApiStatus.AvailableSince("2.6.0")
public final class ServerProperties {
    @Nullable
    private Boolean autostop;

    @SerializedName("autostop-time")
    @Nullable
    private Integer autostopTime;

    @Nullable
    private Boolean autostart;

    @SerializedName("autostart-type")
    @Nullable
    private AutostartType autostartType;

    @SerializedName("launcher-autostart")
    @Nullable
    private Boolean launcherAutostart;

    @SerializedName("launcher-autostart-type")
    @Nullable
    private AutostartType launcherAutostartType;

    @SerializedName("enable-lobby")
    @Nullable
    private Boolean enableLobby;

    @SerializedName("auto-update")
    @Nullable
    private AutoUpdateMode autoUpdate;

    @SerializedName("force-upgrade")
    @Nullable
    private Boolean forceUpgrade;

    /**
     * Create an empty set of server properties. Use the setters to set the properties you want to change.
     */
    public ServerProperties() {
    }

    /**
     * @return whether the server stops automatically when no players are online
     */
    public Optional<Boolean> getAutostop() {
        return Optional.ofNullable(autostop);
    }

    /**
     * @param autostop whether the server stops automatically when no players are online
     * @return this
     */
    public ServerProperties setAutostop(@Nullable Boolean autostop) {
        this.autostop = autostop;
        return this;
    }

    /**
     * @return minutes without players after which the server stops automatically
     */
    public Optional<Integer> getAutostopTime() {
        return Optional.ofNullable(autostopTime);
    }

    /**
     * @param autostopTime minutes without players after which the server stops automatically (3 - 1440)
     * @return this
     */
    public ServerProperties setAutostopTime(@Nullable Integer autostopTime) {
        this.autostopTime = autostopTime;
        return this;
    }

    /**
     * @return whether the server starts automatically when a player joins
     */
    public Optional<Boolean> getAutostart() {
        return Optional.ofNullable(autostart);
    }

    /**
     * @param autostart whether the server starts automatically when a player joins
     * @return this
     */
    public ServerProperties setAutostart(@Nullable Boolean autostart) {
        this.autostart = autostart;
        return this;
    }

    /**
     * @return which players can start the server automatically
     */
    public Optional<AutostartType> getAutostartType() {
        return Optional.ofNullable(autostartType);
    }

    /**
     * @param autostartType which players can start the server automatically. Only the types supported by the server
     *                      are accepted.
     * @return this
     */
    public ServerProperties setAutostartType(@Nullable AutostartType autostartType) {
        this.autostartType = autostartType;
        return this;
    }

    /**
     * @return whether the server starts automatically when a player joins via the launcher
     */
    public Optional<Boolean> getLauncherAutostart() {
        return Optional.ofNullable(launcherAutostart);
    }

    /**
     * @param launcherAutostart whether the server starts automatically when a player joins via the launcher
     * @return this
     */
    public ServerProperties setLauncherAutostart(@Nullable Boolean launcherAutostart) {
        this.launcherAutostart = launcherAutostart;
        return this;
    }

    /**
     * @return which players can start the server automatically via the launcher
     */
    public Optional<AutostartType> getLauncherAutostartType() {
        return Optional.ofNullable(launcherAutostartType);
    }

    /**
     * @param launcherAutostartType which players can start the server automatically via the launcher
     * @return this
     */
    public ServerProperties setLauncherAutostartType(@Nullable AutostartType launcherAutostartType) {
        this.launcherAutostartType = launcherAutostartType;
        return this;
    }

    /**
     * @return whether players joining the offline server see a lobby
     */
    public Optional<Boolean> getEnableLobby() {
        return Optional.ofNullable(enableLobby);
    }

    /**
     * @param enableLobby whether players joining the offline server see a lobby
     * @return this
     */
    public ServerProperties setEnableLobby(@Nullable Boolean enableLobby) {
        this.enableLobby = enableLobby;
        return this;
    }

    /**
     * @return whether the server software is updated automatically
     */
    public Optional<AutoUpdateMode> getAutoUpdate() {
        return Optional.ofNullable(autoUpdate);
    }

    /**
     * @param autoUpdate whether the server software is updated automatically. Changing this requires the software
     *                   permission instead of options.
     * @return this
     */
    public ServerProperties setAutoUpdate(@Nullable AutoUpdateMode autoUpdate) {
        this.autoUpdate = autoUpdate;
        return this;
    }

    /**
     * @return whether the world is upgraded to the current version when the server starts
     */
    public Optional<Boolean> getForceUpgrade() {
        return Optional.ofNullable(forceUpgrade);
    }

    /**
     * @param forceUpgrade whether the world is upgraded to the current version when the server starts
     * @return this
     */
    public ServerProperties setForceUpgrade(@Nullable Boolean forceUpgrade) {
        this.forceUpgrade = forceUpgrade;
        return this;
    }
}
