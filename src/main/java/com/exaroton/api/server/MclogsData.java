package com.exaroton.api.server;

@SuppressWarnings("unused")
public final class MclogsData {
    private String id;
    private String url;
    private String raw;

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
