package com.exaroton.api.ws.stream;

import com.exaroton.api.ws.WebSocketConnection;
import com.exaroton.api.ws.data.TickData;
import com.exaroton.api.ws.subscriber.TickSubscriber;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class TickStream extends Stream<TickSubscriber> {
    public TickStream(@NotNull WebSocketConnection ws, @NotNull Gson gson) {
        super(ws, gson);
    }

    @Override
    protected void onDataMessage(String type, JsonObject message) {
        switch (type) {
            case "tick":
                TickData tick = gson.fromJson(message.get("data"), TickData.class);

                for (TickSubscriber subscriber : getSubscribers()) {
                    subscriber.handleTickData(tick);
                }
                break;
        }
    }

    @Override
    public StreamType getType() {
        return StreamType.TICK;
    }
}
