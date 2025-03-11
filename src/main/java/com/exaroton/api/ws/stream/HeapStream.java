package com.exaroton.api.ws.stream;

import com.exaroton.api.ws.WebSocketConnection;
import com.exaroton.api.ws.data.HeapUsage;
import com.exaroton.api.ws.subscriber.HeapSubscriber;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public final class HeapStream extends Stream<HeapSubscriber> {
    @ApiStatus.Internal
    public HeapStream(@NotNull WebSocketConnection ws, @NotNull Gson gson) {
        super(ws, gson);
    }

    @Override
    protected void onDataMessage(String type, JsonObject message) {
        switch (type) {
            case "heap":
                HeapUsage usage = gson.fromJson(message.get("data"), HeapUsage.class);

                for (HeapSubscriber subscriber : getSubscribers()) {
                    subscriber.handleHeapUsage(usage);
                }
                break;
        }
    }

    @Override
    public StreamType getType() {
        return StreamType.HEAP;
    }
}
