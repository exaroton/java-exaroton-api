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

    public PlayerList(String name, String server, ExarotonClient client) {
        if (name == null) throw new IllegalArgumentException("List name can't be null");
        this.name = name;
        this.server = server;
        this.client = client;
    }

    public String getName() {
        return name;
    }


    public String[] getEntries() throws APIException {
        GetPlayerListEntriesRequest request = new GetPlayerListEntriesRequest(this.client, this.server, this.name);
        return request.request().getData();
    }

    public void add(String[] entries) throws APIException {
        AddPlayerListEntriesRequest request = new AddPlayerListEntriesRequest(this.client, this.server, this.name, entries);
        request.request();
    }

    public void add(String entry) throws APIException {
        this.add(new String[]{entry});
    }

    public void remove(String[] entries) throws APIException {
        RemovePlayerListEntriesRequest request = new RemovePlayerListEntriesRequest(this.client, this.server, this.name, entries);
        request.request();
    }

    public void remove(String entry) throws APIException {
        this.remove(new String[]{entry});
    }

}
