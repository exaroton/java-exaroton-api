package com.exaroton.api.server;

import com.exaroton.api.APIException;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.request.server.AddPlayerListEntriesRequest;
import com.exaroton.api.request.server.GetPlayerListEntriesRequest;
import com.exaroton.api.request.server.RemovePlayerListEntriesRequest;

public class PlayerList {
    private final String name;
    private final String server;
    private final ExarotonClient client;

    /**
     * create a new playerlist
     * @param name playerlist name (see Server.getPlayerLists)
     * @param server exaroton server
     * @param client exaroton client
     */
    public PlayerList(String name, String server, ExarotonClient client) {
        if (name == null) throw new IllegalArgumentException("List name can't be null");
        this.name = name;
        this.server = server;
        this.client = client;
    }

    /**
     * @return list name
     */
    public String getName() {
        return name;
    }

    /**
     * @return players in list
     * @throws APIException
     */
    public String[] getEntries() throws APIException {
        GetPlayerListEntriesRequest request = new GetPlayerListEntriesRequest(this.client, this.server, this.name);
        return request.request().getData();
    }

    /**
     * add players to list
     * @throws APIException
     */
    public void add(String[] entries) throws APIException {
        AddPlayerListEntriesRequest request = new AddPlayerListEntriesRequest(this.client, this.server, this.name, entries);
        request.request();
    }

    /**
     * add player to list
     * @throws APIException
     */
    public void add(String entry) throws APIException {
        this.add(new String[]{entry});
    }

    /**
     * remove players from list
     * @throws APIException
     */
    public void remove(String[] entries) throws APIException {
        RemovePlayerListEntriesRequest request = new RemovePlayerListEntriesRequest(this.client, this.server, this.name, entries);
        request.request();
    }

    /**
     * remove player from list
     * @throws APIException
     */
    public void remove(String entry) throws APIException {
        this.remove(new String[]{entry});
    }

}
