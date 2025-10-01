package com.exaroton.api.ws.stream;

import com.exaroton.api.server.ServerStatus;
import com.exaroton.api.ws.WebSocketConnection;
import com.exaroton.api.ws.subscriber.ManagementNotificationSubscriber;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@ApiStatus.Internal
public final class ServerManagementStream extends Stream<ManagementNotificationSubscriber> {
    private final Map<UUID, CompletableFuture<JsonElement>> waitingForResponse = new ConcurrentHashMap<>();

    public ServerManagementStream(@NotNull WebSocketConnection ws, @NotNull Gson gson) {
        super(ws, gson);
    }

    @Override
    public StreamType getType() {
        return StreamType.MANAGEMENT;
    }

    @Override
    protected void onDataMessage(String type, JsonObject message) {
        var data = message.getAsJsonObject("data");
        switch (type) {
            case "notification":
                for (ManagementNotificationSubscriber subscriber : getSubscribers()) {
                    subscriber.handleNotification(data.get("name").getAsString(), data.get("data"));
                }
                break;
            case "response":
                var id = UUID.fromString(data.get("id").getAsString());
                var future = waitingForResponse.remove(id);
                if (future != null) {
                    future.complete(data.get("data"));
                }
                break;
        }
    }

    @Override
    protected Set<ServerStatus> getStartableStatuses() {
        return Set.of(ServerStatus.ONLINE);
    }

    @Override
    public boolean hasNoSubscribers() {
        return false;
    }

    public CompletableFuture<JsonElement> sendRequest(@NotNull String method, @Nullable JsonElement params) {
        return this.shouldBeStarted().thenCompose(shouldStart -> {
            if (!shouldStart) {
                throw new IllegalStateException("The management stream is not active.");
            }

            var future = new CompletableFuture<JsonElement>();
            var id = UUID.randomUUID();
            waitingForResponse.put(id, future);
            var item = new Request(id, method, params);
            this.sendWhenStarted(messageData("request", item));
            return future;
        });
    }

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private static final class Request {
        private final UUID id;
        private final String method;
        private final JsonElement params;

        public Request(UUID id, String method, JsonElement params) {
            this.id = id;
            this.method = method;
            this.params = params;
        }
    }
}
