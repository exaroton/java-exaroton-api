package com.exaroton.api.server;

import org.jetbrains.annotations.ApiStatus;

public final class MclogsData {
    private final String id;
    private final String url;
    private final String raw;

    @ApiStatus.Internal
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
