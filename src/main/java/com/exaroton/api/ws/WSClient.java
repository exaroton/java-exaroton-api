package com.exaroton.api.ws;

import com.exaroton.api.server.Server;
import com.exaroton.api.ws.stream.ConsoleStream;
import com.exaroton.api.ws.stream.ServerStatusStream;
import com.exaroton.api.ws.data.ConsoleStreamData;
import com.exaroton.api.ws.data.ServerStatusStreamData;
import com.exaroton.api.ws.subscriber.ConsoleSubscriber;
import com.exaroton.api.ws.subscriber.ServerStatusSubscriber;
import com.google.gson.Gson;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class WSClient extends WebSocketClient {

    /**
     * should the web socket client automatically reconnect?
     */
    private boolean autoReconnect = true;

    /**
     * active server status stream
     */
    private ServerStatusStream serverStatusStream;

    /**
     * active console stream
     */
    private ConsoleStream consoleStream;

    /**
     * exaroton server
     */
    private final Server server;

    /**
     *
     * @param uri websocket uri
     * @param server exaroton server
     */
    public WSClient(URI uri, Server server) {
        super(uri);
        this.server = server;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {}

    @Override
    public void onMessage(String message) {
        System.out.println(message);
        WSMessage m = (new Gson()).fromJson(message, WSMessage.class);
        switch (m.getType()) {
            case "keep-alive":
            case "ready":
            case "connected":
            case "disconnected":
                break;

            case "status":
                if (this.serverStatusStream == null) return;
                Server oldServer = new Server(server.getClient(), server.getId())
                        .setFromObject(server);
                this.server.setFromObject((new Gson()).fromJson(message, ServerStatusStreamData.class).getData());
                for (ServerStatusSubscriber subscriber: this.serverStatusStream.subscribers) {
                    subscriber.statusUpdate(oldServer, this.server);
                }
                break;

            case "line":
                if (this.consoleStream == null) return;
                String line = ((new Gson()).fromJson(message, ConsoleStreamData.class)).getData();
                for (ConsoleSubscriber subscriber: consoleStream.subscribers) {
                    subscriber.line(line);
                }
                break;
            case "heap":
                //heap
                break;
            case "tick":
                //tick
                break;

            default:
                System.out.println(m.getType());
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        if (this.autoReconnect && remote) {
            this.reconnect();
        }
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    /**
     * en-/disable auto-reconnect
     * @param autoReconnect should auto-reconnect be enabled
     */
    public void setAutoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
    }

    /**
     * subscribe to server status changes
     * @param subscriber instance of class handling server status changes
     */
    public void addServerStatusSubscriber(ServerStatusSubscriber subscriber) {
        if (this.serverStatusStream == null) this.serverStatusStream = new ServerStatusStream(this);
        this.serverStatusStream.subscribers.add(subscriber);
    }

    /**
     * subscribe to new console lines
     * @param subscriber instance of class handling new console lines
     */
    public void addConsoleSubscriber(ConsoleSubscriber subscriber) {
        if (this.consoleStream == null) this.consoleStream = new ConsoleStream(this);
        this.consoleStream.subscribers.add(subscriber);
    }
}
