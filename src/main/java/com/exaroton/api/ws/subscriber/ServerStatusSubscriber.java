package com.exaroton.api.ws.subscriber;

import com.exaroton.api.server.Server;

public interface ServerStatusSubscriber {

    /**
     * handle status update
     * @param oldServer old server status
     * @param newServer new server status
     */
    void handleStatusUpdate(Server oldServer, Server newServer);
}
