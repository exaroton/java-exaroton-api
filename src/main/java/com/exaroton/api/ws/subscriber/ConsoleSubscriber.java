package com.exaroton.api.ws.subscriber;

public abstract class ConsoleSubscriber {

    /**
     * handle new console line
     * @param line new console line
     */
    public abstract void line(String line);
}
