package com.exaroton.api.ws;

public class WSMessage {

    /**
     * message type
     */
    private final String type;

    public WSMessage(String type) {
        this.type = type;
    }

    /**
     * @return message type
     */
    public String getType() {
        return type;
    }
}
