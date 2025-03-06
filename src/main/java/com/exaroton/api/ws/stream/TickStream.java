package com.exaroton.api.ws.stream;

import com.exaroton.api.ws.WebSocketConnection;
import com.exaroton.api.ws.data.TickData;
import com.exaroton.api.ws.data.TickStreamData;
import com.exaroton.api.ws.subscriber.TickSubscriber;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public final class TickStream extends Stream<TickSubscriber> {
    @ApiStatus.Internal
    public TickStream(@NotNull WebSocketConnection ws, @NotNull Gson gson) {
        super(ws, gson);
    }

    @Override
    protected void onDataMessage(String type, JsonObject message) {
        switch (type) {
            case "tick":
                TickData tick = gson.fromJson(message, TickStreamData.class).getData();

                for (TickSubscriber subscriber : subscribers) {
                    subscriber.handleTickData(tick);
                }
                break;
        }
    }

    @Override
    protected StreamType getType() {
        return StreamType.TICK;
    }
}
