package com.exaroton.api.ws;

public class WSMessage {
    private final String type;

    public WSMessage(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
