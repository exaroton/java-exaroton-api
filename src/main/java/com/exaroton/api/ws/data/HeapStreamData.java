package com.exaroton.api.ws.data;

public class HeapStreamData extends StreamData<HeapUsage> {
    public HeapStreamData(String stream, String type, HeapUsage data) {
        super(stream, type, data);
    }
}
