package com.exaroton.api.ws.data;

public class StatsStreamData extends StreamData<StatsData> {
    public StatsStreamData(String stream, String type, StatsData data) {
        super(stream, type, data);
    }
}
