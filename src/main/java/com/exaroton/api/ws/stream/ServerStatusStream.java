package com.exaroton.api.ws.stream;

import com.exaroton.api.server.Server;
import com.exaroton.api.ws.WebSocketConnection;
import com.exaroton.api.ws.data.ServerStatusStreamData;
import com.exaroton.api.ws.subscriber.ServerStatusSubscriber;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

@ApiStatus.Internal
public final class ServerStatusStream extends Stream<ServerStatusSubscriber> {
    private Server server;

    public ServerStatusStream(@NotNull WebSocketConnection ws, @NotNull Gson gson) {
        super(ws, gson);
    }

    public ServerStatusStream setServer(Server server) {
        this.server = server;
        return this;
    }

    @Override
    protected void onDataMessage(String type, JsonObject message) {
        switch (type) {
            case "status":
                Server oldServer = new Server(server.getClient(), gson, server.getId()).setFromObject(server);
                this.server.setFromObject(gson.fromJson(message, ServerStatusStreamData.class).getData());

                ws.autoStartStop();

                for (ServerStatusSubscriber subscriber : getSubscribers()) {
                    subscriber.handleStatusUpdate(oldServer, this.server);
                }
                break;
        }
    }

    @Override
    public StreamType getType() {
        return StreamType.STATUS;
    }

    @Override
    public CompletableFuture<Void> tryToStart() {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> tryToStop() {
        return CompletableFuture.completedFuture(null);
    }
}
