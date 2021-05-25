package com.exaroton.api.ws;

import com.exaroton.api.server.Server;
import com.exaroton.api.ws.data.*;
import com.exaroton.api.ws.stream.*;
import com.exaroton.api.ws.subscriber.*;
import com.google.gson.Gson;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.Locale;

public class WSClient extends WebSocketClient {

    /**
     * active server status stream
     */
    private ServerStatusStream serverStatusStream;

    /**
     * active console stream
     */
    private ConsoleStream consoleStream;

    /**
     * active heap stream
     */
    private HeapStream heapStream;

    /**
     * active stats stream
     */
    private StatsStream statsStream;

    /**
     * active tick stream
     */
    private TickStream tickStream;

    /**
     * exaroton server
     */
    private final Server server;

    /**
     * is the connection ready
     */
    private boolean ready = false;

    /**
     * messages to send once the connection becomes ready
     */
    private final ArrayList<String> messages = new ArrayList<>();

    /**
     * logger
     */
    private final Logger logger =  LoggerFactory.getLogger("java-exaroton-api");

    /**
     * @param uri websocket uri
     * @param server exaroton server
     */
    public WSClient(URI uri, Server server) {
        super(uri);
        this.server = server;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        logger.info("Connected to websocket!");
    }

    @Override
    public void onMessage(String message) {
        WSMessage m = (new Gson()).fromJson(message, WSMessage.class);
        switch (m.getType()) {
            case "keep-alive":
            case "connected":
            case "disconnected":
                break;

            case "ready":
                ready = true;
                for (String data : this.messages) {
                    this.send(data);
                }
                this.messages.clear();
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
                String line = (new Gson()).fromJson(message, ConsoleStreamData.class).getData();
                for (ConsoleSubscriber subscriber: consoleStream.subscribers) {
                    subscriber.line(line);
                }
                break;
            case "heap":
                if (this.heapStream == null) return;
                HeapUsage usage = (new Gson()).fromJson(message, HeapStreamData.class).getData();
                for (HeapSubscriber subscriber : heapStream.subscribers) {
                    subscriber.heap(usage);
                }
                break;

            case "stats":
                if (this.statsStream == null) return;
                StatsData stats = (new Gson()).fromJson(message, StatsStreamData.class).getData();
                for (StatsSubscriber subscriber : statsStream.subscribers) {
                    subscriber.stats(stats);
                }
                break;

            case "tick":
                if (this.tickStream == null) return;
                TickData tick = (new Gson()).fromJson(message, TickStreamData.class).getData();
                for (TickSubscriber subscriber: tickStream.subscribers) {
                    subscriber.tick(tick);
                }
                break;
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.info("Websocket disconnected with code " + code + (reason.length() > 0 ? ": " + reason : ""));
    }

    @Override
    public void onError(Exception ex) {
        logger.error("A websocket error ocurred", ex);
    }

    /**
     * send data once connection is ready
     * @param data web socket message
     */
    public void sendWhenReady(String data) {
        if (this.ready)
            this.send(data);
        else
            this.messages.add(data);
    }

    /**
     * subscribe to a stream if it is not already active
     * @param stream stream name
     */
    public void subscribe(String stream) {
        if (stream == null) throw new IllegalArgumentException("No stream specified");

        switch (stream.toLowerCase(Locale.ROOT)) {
            case "console":
                if (consoleStream == null) {
                    consoleStream = new ConsoleStream(this);
                    consoleStream.start();
                }
                break;

            case "heap":
                if (heapStream == null) {
                    heapStream = new HeapStream(this);
                    heapStream.start();
                }
                break;

            case "stats":
                if (statsStream == null) {
                    statsStream = new StatsStream(this);
                    statsStream.start();
                }
                break;

            case "tick":
                if (tickStream == null) {
                    tickStream = new TickStream(this);
                    tickStream.start();
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown stream");
        }
    }

    /**
     * unsubscribe from a stream
     * @param stream stream name
     */
    public void unsubscribe(String stream) {
        if (stream == null) throw new IllegalArgumentException("No stream specified");

        switch (stream.toLowerCase(Locale.ROOT)) {
            case "console":
                if (consoleStream != null) {
                    consoleStream.stop();
                    consoleStream = null;
                }
                break;

            case "heap":
                if (heapStream != null) {
                    heapStream.stop();
                    heapStream = null;
                }
                break;

            case "stats":
                if (statsStream != null) {
                    statsStream.stop();
                    statsStream = null;
                }
                break;

            case "tick":
                if (tickStream != null) {
                    tickStream.stop();
                    tickStream = null;
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown stream");
        }
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
        if (this.consoleStream == null) throw new RuntimeException("There is no active console stream");
        this.consoleStream.subscribers.add(subscriber);
    }

    /**
     * subscribe to heap data
     * @param subscriber instance of class handling heap data
     */
    public void addHeapSubscriber(HeapSubscriber subscriber) {
        if (this.heapStream == null) throw new RuntimeException("There is no active heap stream");
        this.heapStream.subscribers.add(subscriber);
    }

    /**
     * subscribe to stats
     * @param subscriber instance of class handling stats
     */
    public void addStatsSubscriber(StatsSubscriber subscriber) {
        if (this.statsStream == null) throw new RuntimeException("There is no active stats stream");
        this.statsStream.subscribers.add(subscriber);
    }

    /**
     * subscribe to stats
     * @param subscriber instance of class handling stats
     */
    public void addTickSubscriber(TickSubscriber subscriber) {
        if (this.tickStream == null) throw new RuntimeException("There is no active tick stream");
        this.tickStream.subscribers.add(subscriber);
    }

    /**
     * execute a command using the console stream if it is active
     * @param command minecraft command
     * @return was the command executed
     */
    public boolean executeCommand(String command) {
        if (this.consoleStream == null) {
            return false;
        }
        else {
            this.consoleStream.executeCommand(command);
            return true;
        }
    }
}
