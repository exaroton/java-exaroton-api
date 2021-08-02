package com.exaroton.api.ws;

import com.exaroton.api.server.Server;
import com.exaroton.api.ws.data.*;
import com.exaroton.api.ws.stream.*;
import com.exaroton.api.ws.subscriber.*;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class WebSocketManager {

    private final WebSocketClient client;

    private Timer reconnectTimer;

    private boolean autoReconnect = true;

    /**
     * messages to send once the connection becomes ready
     */
    private final ArrayList<String> messages = new ArrayList<>();

    /**
     * is the connection ready
     */
    private boolean ready = false;

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
     * logger
     */
    private final Logger logger =  LoggerFactory.getLogger("java-exaroton-api");

    public WebSocketManager(String uri, String apiToken, Server server) {
        try {
            URI u = new URI(uri);
            this.client = new WebSocketClient(u, this);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to connect to websocket", e);
        }
        this.client.addHeader("Authorization", "Bearer " + apiToken);
        this.client.connect();
        this.server = server;
    }

    /**
     * handle websocket data
     * @param type message type
     * @param message raw message
     */
    public void handleData(String type, String message) {
        switch (type) {
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

    /**
     * handle closed websocket connection
     * reconnect if enabled
     * @param code disconnect code
     * @param reason disconnect reason
     * @param remote closing side
     */
    public void handleClose(int code, String reason, boolean remote) {
        logger.info("Websocket disconnected with code " + code + (reason.length() > 0 ? ": " + reason : ""));
        if (remote && this.shouldAutoReconnect()) {
            reconnectTimer = new Timer();
            reconnectTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    logger.info("Trying to reconnect...");
                    client.reconnect();
                }
            }, 0, 5000);
        }
    }

    /**
     * handle an opened connection
     */
    public void handleOpen() {
        if (this.reconnectTimer != null) this.reconnectTimer.cancel();
    }

    /**
     * subscribe to a stream if it is not already active
     * @param stream stream name
     */
    public void subscribe(String stream) {
        if (stream == null) throw new IllegalArgumentException("No stream specified");

        switch (stream.toLowerCase(Locale.ROOT)) {

            case "ready":
                ready = true;
                for (String data : this.messages) {
                    this.client.send(data);
                }
                this.messages.clear();
                break;

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

    /**
     * send data once connection is ready
     * @param data web socket message
     */
    public void sendWhenReady(String data) {
        if (this.ready)
            this.client.send(data);
        else
            this.messages.add(data);
    }

    /**
     * en-/disable auto reconnect
     * @param autoReconnect new reconnect state
     */
    public void setAutoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
    }

    /**
     * @return is auto reconnect enabled
     */
    public boolean shouldAutoReconnect() {
        return autoReconnect;
    }

    /**
     * close websocket connection
     */
    public void close() {
        if (this.reconnectTimer != null) this.reconnectTimer.cancel();
        this.client.close();
    }
}
