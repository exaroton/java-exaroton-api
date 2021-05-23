package com.exaroton.api.ws.data;

public class StreamData<Datatype> {

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
    private Datatype data;

    public StreamData(String stream, String type) {
        this.stream = stream;
        this.type = type;
    }

    public StreamData(String stream, String type, Datatype data) {
        this.stream = stream;
        this.type = type;
        this.data = data;
    }

    /**
     * @return message type
     */
    public String getType() {
        return type;
    }

    /**
     * @return stream name
     */
    public String getStream() {
        return stream;
    }

    /**
     * @return stream data
     */
    public Datatype getData() {
        return data;
    }
}
