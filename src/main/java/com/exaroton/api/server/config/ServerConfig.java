package com.exaroton.api.server.config;

import com.exaroton.api.ExarotonClient;
import com.exaroton.api.util.ParameterValidator;
import com.exaroton.api.request.server.files.GetConfigOptionsRequest;
import com.exaroton.api.request.server.files.UpdateConfigOptionsRequest;
import com.exaroton.api.server.Server;
import com.google.gson.Gson;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public final class ServerConfig {
    private final ExarotonClient client;
    private final Gson gson;
    private final Server server;
    private final String path;
    private Map<String, ConfigOption<?>> options = null;

    @ApiStatus.Internal
    public ServerConfig(
            @NotNull ExarotonClient client,
            @NotNull Gson gson,
            @NotNull Server server,
            @NotNull String path
    ) {
        this.client = client;
        this.gson = gson;
        this.server = server;
        this.path = ParameterValidator.requireNonEmpty(path, "path");
    }

    /**
     * Get the options of the server config either by fetching them from the API or from the local cache
     * @return a map of the options
     * @throws IOException if an error occurs while fetching the options
     */
    public CompletableFuture<Map<String, ConfigOption<?>>> getOptions() throws IOException {
        return this.getOptions(false);
    }

    /**
     * Get the options of the server config either by fetching them from the API or from the local cache
     * @param update if true the cache will not be used
     * @return a map of the options
     * @throws IOException if an error occurs while fetching the options
     */
    public CompletableFuture<Map<String, ConfigOption<?>>> getOptions(boolean update) throws IOException {
        if (this.options == null || update) {
            return this.fetchOptions().thenApply(x -> this.options);
        }
        return CompletableFuture.completedFuture(this.options);
    }

    private CompletableFuture<Void> fetchOptions() throws IOException {
        return client.request(new GetConfigOptionsRequest(this.client, this.gson, this.server.getId(), this.path))
                        .thenAccept(this::setOptions);
    }

    /**
     * Set the options of the server config
     * @param options collection of options
     */
    public void setOptions(Collection<ConfigOption<?>> options) {
        this.options = new HashMap<>();
        for (ConfigOption<?> option : options) {
            this.options.put(option.getKey(), option);
        }
    }

    /**
     * Set the options of the server config
     * @param options map of options
     */
    public void setOptions(Map<String, ConfigOption<?>> options) {
        this.options = new HashMap<>(options);
    }

    /**
     * Get a specifc option by its key either from the API or the local cache
     * @param key the key of the option
     * @param update if true the cache will not be used
     * @return the option
     * @throws IOException if an error occurs while fetching the option
     */
    public CompletableFuture<ConfigOption<?>> getOption(String key, boolean update) throws IOException {
        return this.getOptions(update).thenApply(options -> options.get(key));
    }

    /**
     * Get a specifc option by its key either from the API or the local cache
     * @param key the key of the option
     * @return the option
     * @throws IOException if an error occurs while fetching the option
     */
    public CompletableFuture<ConfigOption<?>> getOption(String key) throws IOException {
        return this.getOption(key, false);
    }

    /**
     * Save the config options to the server
     * @return a completable future that completes when the options are saved
     * @throws IOException if an error occurs while saving the options
     */
    public CompletableFuture<Void> save() throws IOException {
        Map<String, Object> options = new HashMap<>();
        for (ConfigOption<?> option : this.options.values()) {
            if (option.getValue() != null) {
                options.put(option.getKey(), option.getValue());
            }
        }

        return client.request(new UpdateConfigOptionsRequest(this.client, this.gson, this.server.getId(), this.path, options))
                        .thenAccept(this::setOptions);
    }
}
