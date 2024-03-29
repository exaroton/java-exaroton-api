package com.exaroton.api.ws;

import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.Server;
import com.exaroton.api.ws.data.*;
import com.exaroton.api.ws.stream.*;
import com.exaroton.api.ws.subscriber.*;
import com.google.gson.Gson;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class WebSocketManager {
    private final ExarotonClient exaroton;

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
     * active streams
     */
    private final Map<StreamName, Stream> streams = new HashMap<>();

    /**
     * exaroton server
     */
    private final Server server;

    private ErrorListener errorListener = null;

    private DebugListener debugListener = null;

    public WebSocketManager(ExarotonClient exaroton, String uri, String apiToken, Server server) {
        this.exaroton = exaroton;
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

    public Gson getGson() {
        return exaroton.getGson();
    }

    /**
     * handle websocket data
     * @param type message type
     * @param message raw message
     */
    public void handleData(String type, String message) {
        final StreamName name = StreamName.get(type);
        final Stream stream = streams.get(name);
        switch (type) {
            case "disconnected":
                break;

            case "ready":
                ready = true;
                for (String data : this.messages) {
                    this.client.send(data);
                }
                this.messages.clear();
                break;

            case "status":
                Server oldServer = new Server(server.getClient(), server.getId())
                        .setFromObject(server);
                this.server.setFromObject(exaroton.getGson().fromJson(message, ServerStatusStreamData.class).getData());

                //start/stop streams based on status
                for (Stream s: streams.values()) {
                    s.onStatusChange();
                }

                if (stream == null) return;
                for (Object subscriber: stream.subscribers) {
                    ((ServerStatusSubscriber) subscriber).statusUpdate(oldServer, this.server);
                }
                break;

            case "line":
                if (stream == null) return;
                String line = exaroton.getGson().fromJson(message, ConsoleStreamData.class).getData();
                for (Object subscriber: stream.subscribers) {
                    ((ConsoleSubscriber) subscriber).line(line);
                }
                break;
            case "heap":
                if (stream == null) return;
                HeapUsage usage = exaroton.getGson().fromJson(message, HeapStreamData.class).getData();
                for (Object subscriber : stream.subscribers) {
                    ((HeapSubscriber) subscriber).heap(usage);
                }
                break;

            case "stats":
                if (stream == null) return;
                StatsData stats = exaroton.getGson().fromJson(message, StatsStreamData.class).getData();
                for (Object subscriber : stream.subscribers) {
                    ((StatsSubscriber) subscriber).stats(stats);
                }
                break;

            case "tick":
                if (stream == null) return;
                TickData tick = exaroton.getGson().fromJson(message, TickStreamData.class).getData();
                for (Object subscriber: stream.subscribers) {
                    ((TickSubscriber) subscriber).tick(tick);
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
        if (remote && this.shouldAutoReconnect()) {
            reconnectTimer = new Timer();
            this.sendDebug("Reconnecting in 5s");
            reconnectTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    client.reconnect();
                }
            }, 5000, 5000);
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

        Stream s;
        final StreamName name = StreamName.get(stream);
        switch (name) {

            case CONSOLE:
                s = new ConsoleStream(this);
                break;

            case HEAP:
                s = new HeapStream(this);
                break;

            case STATS:
                s = new StatsStream(this);
                break;

            case TICK:
                s = new TickStream(this);
                break;

            default:
                throw new IllegalArgumentException("Unknown stream");
        }

        this.streams.put(name, s);
        s.start();
    }

    /**
     * unsubscribe from a stream
     * @param stream stream name
     */
    public void unsubscribe(String stream) {
        if (stream == null) throw new IllegalArgumentException("No stream specified");

        final StreamName name = StreamName.get(stream);
        Stream s = this.streams.get(name);
        if (s != null) {
            s.stop();
            this.streams.remove(name);
        }
    }

    /**
     * subscribe to server status changes
     * @param subscriber instance of class handling server status changes
     */
    public void addServerStatusSubscriber(ServerStatusSubscriber subscriber) {
        if (!this.streams.containsKey(StreamName.STATUS)) this.streams.put(StreamName.STATUS, new ServerStatusStream(this));
        this.streams.get(StreamName.STATUS).subscribers.add(subscriber);
    }

    public void addStreamSubscriber(StreamName name, Subscriber subscriber) {
        if (!this.streams.containsKey(name)) throw new RuntimeException("There is no active stream for: "+name);
        this.streams.get(name).subscribers.add(subscriber);
    }

    /**
     * subscribe to new console lines
     * @param subscriber instance of class handling new console lines
     */
    public void addConsoleSubscriber(ConsoleSubscriber subscriber) {
        this.addStreamSubscriber(StreamName.CONSOLE, subscriber);
    }

    /**
     * subscribe to heap data
     * @param subscriber instance of class handling heap data
     */
    public void addHeapSubscriber(HeapSubscriber subscriber) {
        this.addStreamSubscriber(StreamName.HEAP, subscriber);
    }

    /**
     * subscribe to stats
     * @param subscriber instance of class handling stats
     */
    public void addStatsSubscriber(StatsSubscriber subscriber) {
        this.addStreamSubscriber(StreamName.STATS, subscriber);
    }

    /**
     * subscribe to stats
     * @param subscriber instance of class handling stats
     */
    public void addTickSubscriber(TickSubscriber subscriber) {
        this.addStreamSubscriber(StreamName.TICK, subscriber);
    }

    /**
     * execute a command using the console stream if it is active
     * @param command minecraft command
     * @return was the command executed
     */
    public boolean executeCommand(String command) {
        Stream s = this.streams.get(StreamName.CONSOLE);
        if (s == null) {
            return false;
        }
        else {
            ((ConsoleStream) s).executeCommand(command);
            return true;
        }
    }

    /**
     * send data once connection is ready
     * @param data web socket message
     */
    public void sendWhenReady(String data) {
        if (this.ready) {
            this.client.send(data);
        }
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

    public boolean serverHasStatus(int... status) {
        if (!this.server.fetched) {
            try {
                this.server.get();
            } catch (Exception ignored) {

            }
        }
        return this.server.hasStatus(status);
    }

    /**
     * Listen to websocket errors
     * @param errorListener the only error listener
     */
    public void setErrorListener(ErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    /**
     * listen to websocket debug information
     * @param debugListener the only debug listener
     */
    public void setDebugListener(DebugListener debugListener) {
        this.debugListener = debugListener;
    }


    /**
     * send debug information to listeners
     */
    void sendDebug(String message) {
        if (this.debugListener != null) {
            this.debugListener.onDebug(message);
        }
    }

    /**
     * send error to listeners
     */
    void onError(String error, Throwable throwable) {
        this.errorListener.onError(error, throwable);
    }
}
