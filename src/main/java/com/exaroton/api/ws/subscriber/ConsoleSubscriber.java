package com.exaroton.api.ws.subscriber;

public interface ConsoleSubscriber {
    /**
     * handle new console line
     * @param line new console line
     */
    void handleLine(String line);
}
