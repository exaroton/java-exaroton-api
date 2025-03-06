package com.exaroton.api.ws.stream;

import com.exaroton.api.ws.WebSocketConnection;
import com.google.gson.Gson;
import org.jetbrains.annotations.ApiStatus;

import java.util.Locale;
import java.util.function.BiFunction;

public enum StreamType {
    CONSOLE("console", ConsoleStream.class, ConsoleStream::new),
    HEAP("heap", HeapStream.class, HeapStream::new),
    STATUS("status", ServerStatusStream.class, ServerStatusStream::new),
    STATS("stats", StatsStream.class, StatsStream::new),
    TICK("tick", TickStream.class, TickStream::new),
    ;

    private final String name;
    private final Class<? extends Stream<?>> streamClass;
    private final BiFunction<WebSocketConnection, Gson, ? extends Stream<?>> constructor;

    StreamType(String name, Class<? extends Stream<?>> streamClass, BiFunction<WebSocketConnection, Gson, ? extends Stream<?>> constructor) {
        this.name = name;
        this.streamClass = streamClass;
        this.constructor = constructor;
    }

    public String getName() {
        return name;
    }

    @ApiStatus.Internal
    public Class<? extends Stream<?>> getStreamClass() {
        return streamClass;
    }

    @ApiStatus.Internal
    public Stream<?> construct(WebSocketConnection ws, Gson gson) {
        return constructor.apply(ws, gson);
    }

    public static StreamType get(String x) {
        if (x == null) {
            return null;
        }

        for (StreamType name : values()) {
            if (name.getName().equals(x.toLowerCase(Locale.ROOT))) {
                return name;
            }
        }

        throw new IllegalArgumentException("Unknown stream name: " + x);
    }
}
