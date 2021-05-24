package com.exaroton.api.ws.data;

public class TickStreamData extends StreamData<TickData> {
    public TickStreamData(String stream, String type, TickData data) {
        super(stream, type, data);
    }
}
