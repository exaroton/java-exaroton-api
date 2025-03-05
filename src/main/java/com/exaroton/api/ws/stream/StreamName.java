package com.exaroton.api.ws.stream;

import java.util.Locale;

public enum StreamName {
    CONSOLE("console"),
    HEAP("heap"),
    STATUS("status"),
    STATS("stats"),
    TICK("tick"),;

    private final String value;

    StreamName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static StreamName get(String x) {
        for (StreamName name : values()) {
            if (name.getValue().equals(x.toLowerCase(Locale.ROOT))) {
                return name;
            }
        }

        throw new IllegalArgumentException("Unknown stream name: " + x);
    }
}
