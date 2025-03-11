package com.exaroton.api.server;

import com.exaroton.api.ExarotonClient;
import com.exaroton.api.util.ParameterValidator;
import com.exaroton.api.request.server.AddPlayerListEntriesRequest;
import com.exaroton.api.request.server.GetPlayerListEntriesRequest;
import com.exaroton.api.request.server.RemovePlayerListEntriesRequest;
import com.google.gson.Gson;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public final class PlayerList {
    private transient final ExarotonClient client;
    private transient final Gson gson;

    private final String name;
    private final String serverId;

    /**
     * Use {@link Server#getPlayerList(String)} to get a player list instead of calling this constructor directly.
     *
     * @param client   exaroton client
     * @param gson     gson
     * @param serverId exaroton server id
     * @param name     player list name see {@link Server#getPlayerLists()})
     */
    @ApiStatus.Internal
    public PlayerList(
            @NotNull ExarotonClient client,
            @NotNull Gson gson,
            @NotNull String serverId,
            @NotNull String name
    ) {
        this.client = Objects.requireNonNull(client);
        this.gson = Objects.requireNonNull(gson);
        this.name = ParameterValidator.requireNonEmpty(name, "name");
        this.serverId = serverId;
    }

    /**
     * @return list name
     */
    public String getName() {
        return name;
    }

    /**
     * @return players in list
     * @throws IOException Connection errors
     */
    public CompletableFuture<List<String>> getEntries() throws IOException {
        return client.request(new GetPlayerListEntriesRequest(this.client, this.gson, this.serverId, this.name));
    }

    /**
     * add players to list
     *
     * @param entries player names
     * @return completable future with all players in the list
     * @throws IOException Connection errors
     */
    public CompletableFuture<List<String>> add(String... entries) throws IOException {
        ParameterValidator.requireNonEmpty(entries, "entries");
        return add(List.of(entries));
    }

    /**
     * add players to list
     *
     * @param entries player names
     * @return completable future with all players in the list
     * @throws IOException Connection errors
     */
    public CompletableFuture<List<String>> add(Collection<String> entries) throws IOException {
        if (entries.isEmpty()) {
            throw new IllegalArgumentException("Can't add empty list");
        }

        return client.request(new AddPlayerListEntriesRequest(this.client, this.gson, this.serverId, this.name, entries));
    }

    /**
     * remove players from list
     *
     * @param entries player names
     * @return completable future with all players in the list
     * @throws IOException Connection errors
     */
    public CompletableFuture<List<String>> remove(String... entries) throws IOException {
        ParameterValidator.requireNonEmpty(entries, "entries");
        return remove(List.of(entries));
    }

    /**
     * remove players from list
     *
     * @param entries player names
     * @return completable future with all players in the list
     * @throws IOException Connection errors
     */
    public CompletableFuture<List<String>> remove(List<String> entries) throws IOException {
        if (entries.isEmpty()) {
            throw new IllegalArgumentException("Can't remove empty list");
        }

        return client.request(new RemovePlayerListEntriesRequest(this.client, this.gson, this.serverId, this.name, entries));
    }
}
