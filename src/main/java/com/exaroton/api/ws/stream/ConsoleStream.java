package com.exaroton.api.ws.stream;

import com.exaroton.api.ws.WebSocketConnection;
import com.exaroton.api.ws.data.ConsoleStreamData;
import com.exaroton.api.ws.subscriber.ConsoleSubscriber;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

@ApiStatus.Internal
public final class ConsoleStream extends Stream<ConsoleSubscriber> {
    public ConsoleStream(@NotNull WebSocketConnection ws, @NotNull Gson gson) {
        super(ws, gson);
    }

    /**
     * execute a command using the websocket
     *
     * @param command minecraft command
     * @return a future that completes when the websocket message was sent
     */
    public CompletableFuture<Void> executeCommand(String command) {
        return this.send("command", command);
    }

    @Override
    protected void onDataMessage(String type, JsonObject message) {
        switch (type) {
            case "line":
                String line = gson.fromJson(message, ConsoleStreamData.class).getData();
                for (ConsoleSubscriber subscriber : subscribers) {
                    subscriber.handleLine(line);
                }
        }
    }

    @Override
    public StreamType getType() {
        return StreamType.CONSOLE;
    }
}
