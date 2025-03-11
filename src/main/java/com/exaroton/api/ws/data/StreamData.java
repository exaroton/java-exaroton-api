package com.exaroton.api.ws.data;

import org.jetbrains.annotations.ApiStatus;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
@ApiStatus.Internal
public final class StreamData<Datatype> {

    /**
     * stream name
     */
    private final String stream;

    /**
     * message type
     */
    private final String type;

    /**
     * data
     */
    private final Datatype data;

    public StreamData(String stream, String type, Datatype data) {
        this.stream = stream;
        this.type = type;
        this.data = data;
    }
}
