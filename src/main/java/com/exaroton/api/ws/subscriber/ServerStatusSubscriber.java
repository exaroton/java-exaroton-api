package com.exaroton.api.ws.subscriber;

import com.exaroton.api.server.Server;

public abstract class ServerStatusSubscriber {

    /**
     * handle status update
     * @param oldServer old server status
     * @param newServer new server status
     */
    public abstract void statusUpdate(Server oldServer, Server newServer);
}
