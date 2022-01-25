package com.exaroton.api.ws.stream;

import java.util.Locale;

public enum StreamName {
    CONSOLE,
    HEAP,
    STATUS,
    STATS,
    TICK;

    public static StreamName get(String x) {
        StreamName res;
        try {
            res = StreamName.valueOf(x.toUpperCase(Locale.ROOT));
        }
        catch (IllegalArgumentException e) {
            res = null;
        }
        return res;
    }
}
