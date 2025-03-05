package com.exaroton.api.server;

import com.exaroton.api.APIException;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.ParameterValidator;
import com.exaroton.api.request.server.AddPlayerListEntriesRequest;
import com.exaroton.api.request.server.GetPlayerListEntriesRequest;
import com.exaroton.api.request.server.RemovePlayerListEntriesRequest;
import com.google.gson.Gson;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class PlayerList {
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
     * @throws APIException API error
     */
    public String[] getEntries() throws APIException {
        GetPlayerListEntriesRequest request = new GetPlayerListEntriesRequest(this.client, this.gson, this.serverId, this.name);
        return request.request().getData();
    }

    /**
     * add players to list
     *
     * @param entries player names
     * @throws APIException API error
     */
    public void add(List<String> entries) throws APIException {
        if (entries.isEmpty()) {
            return;
        }

        AddPlayerListEntriesRequest request = new AddPlayerListEntriesRequest(this.client, this.gson, this.serverId, this.name, entries);
        request.request();
    }

    /**
     * remove players from list
     *
     * @param entries player names
     * @throws APIException API error
     */
    public void remove(List<String> entries) throws APIException {
        if (entries.isEmpty()) {
            return;
        }

        RemovePlayerListEntriesRequest request = new RemovePlayerListEntriesRequest(this.client, this.gson, this.serverId, this.name, entries);
        request.request();
    }
}
