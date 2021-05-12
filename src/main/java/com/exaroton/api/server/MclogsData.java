package com.exaroton.api.server;

public class MclogsData {
    private final String id;
    private final String url;
    private final String raw;

    public MclogsData(String id, String url, String raw) {
        this.id = id;
        this.url = url;
        this.raw = raw;
    }

    /**
     * @return mclo.gs log ID
     */
    public String getId() {
        return id;
    }

    /**
     * @return mclo.gs log URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return mclo.gs raw URL
     */
    public String getRaw() {
        return raw;
    }
}
