package com.exaroton.api.server;

import org.jetbrains.annotations.ApiStatus;

public final class ServerLog {
    private final String content;

    @ApiStatus.Internal
    public ServerLog(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
