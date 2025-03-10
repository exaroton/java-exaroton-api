package com.exaroton.api.ws.stream;

import com.exaroton.api.ws.WebSocketConnection;
import com.exaroton.api.ws.data.StatsData;
import com.exaroton.api.ws.data.StatsStreamData;
import com.exaroton.api.ws.subscriber.StatsSubscriber;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class StatsStream extends Stream<StatsSubscriber> {
    public StatsStream(@NotNull WebSocketConnection ws, @NotNull Gson gson) {
        super(ws, gson);
    }

    @Override
    protected void onDataMessage(String type, JsonObject message) {
        switch (type) {
            case "stats":
                StatsData stats = gson.fromJson(message, StatsStreamData.class).getData();

                for (StatsSubscriber subscriber : getSubscribers()) {
                    subscriber.handleStats(stats);
                }
                break;
        }
    }

    @Override
    public StreamType getType() {
        return StreamType.STATS;
    }
}
