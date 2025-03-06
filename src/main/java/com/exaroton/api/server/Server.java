package com.exaroton.api.server;

import com.exaroton.api.ExarotonClient;
import com.exaroton.api.Initializable;
import com.exaroton.api.request.server.*;
import com.exaroton.api.ws.WebSocketConnection;
import com.exaroton.api.ws.stream.StreamName;
import com.exaroton.api.ws.subscriber.*;
import com.google.gson.Gson;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class Server implements Initializable {

    /**
     * has this server been fetched from the API yet
     */
    private boolean fetched = false;

    /**
     * Unique server ID
     */
    private String id;

    /**
     * Server  name
     */
    private String name;

    /**
     * Full server address (e.g. example.exaroton.me)
     */
    private String address;

    /**
     * Server MOTD
     */
    private String motd;

    /**
     * Server status code
     * see StatusCode
     */
    private int status;

    /**
     * Information about players
     */
    private PlayerInfo players;

    /**
     * Host address
     * Only available if the server is online
     */
    private String host;

    /**
     * Server port
     * Only available if the server is online
     */
    private int port;

    /**
     * Information about the installed server software
     */
    private ServerSoftware software;

    /**
     * Whether the server is accessed via the Share Access feature
     */
    private boolean shared;

    /**
     * the client used to create this server
     */
    private transient ExarotonClient client;

    /**
     * web socket client
     */
    private transient WebSocketConnection webSocket;

    /**
     * gson instance
     */
    private transient Gson gson;

    /**
     * Create a new server object
     *
     * @param client exaroton client that will be used for requests
     * @param gson   gson instance
     * @param id     server id
     */
    @ApiStatus.Internal
    public Server(@NotNull ExarotonClient client, @NotNull Gson gson, @NotNull String id) {
        this.client = Objects.requireNonNull(client);
        this.gson = Objects.requireNonNull(gson);
        this.id = Objects.requireNonNull(id);
    }

    /**
     * Check if this server has been fetched from the API.
     * If not, some methods may not work as expected.
     * @return true if the server has been fetched
     */
    public boolean isFetched() {
        return fetched;
    }

    /**
     * Get the server id
     *
     * @return unique server id (tgkm731xO7GiHt76)
     */
    public String getId() {
        return id;
    }

    /**
     * Get the server name
     *
     * @return unique server name (example)
     */
    public String getName() {
        return name;
    }

    /**
     * Get the server address
     *
     * @return unique server address (example.exaroton.com)
     */
    public String getAddress() {
        return address;
    }

    /**
     * Get the current server status
     *
     * @return status or OFFLINE if the status is unknown
     */
    public ServerStatus getStatus() {
        return ServerStatus.fromValue(status).orElse(ServerStatus.OFFLINE);
    }

    /**
     * check if the server has this status
     *
     * @param status status
     * @return true if the status matches
     */
    public boolean hasStatus(ServerStatus... status) {
        return hasStatus(Set.of(status));
    }

    /**
     * check if the server has one of the given statuses
     * @param status status
     * @return true if the status matches
     */
    public boolean hasStatus(Set<ServerStatus> status) {
        return Objects.requireNonNull(status).contains(this.getStatus());
    }

    /**
     * Get the MOTD (message of the day)
     *
     * @return server MOTD
     */
    public String getMotd() {
        return motd;
    }

    /**
     * Fetch the MOTD from the API
     * To retrieve the cached MOTD use {@link #getMotd()}
     *
     * @return server MOTD
     * @throws IOException connection errors
     */
    public CompletableFuture<ServerMOTDInfo> fetchMotd() throws IOException {
        return client.request(new GetServerMOTDRequest(this.client, this.gson, this.id))
                .thenApply(data -> {
                    this.motd = data.getMotd();
                    return data;
                });
    }

    /**
     * Set the server MOTD
     *
     * @param motd new server MOTD
     * @return updated server MOTD
     * @throws IOException connection errors
     */
    public CompletableFuture<ServerMOTDInfo> setMotd(String motd) throws IOException {
        return client.request(new SetServerMOTDRequest(this.client, this.gson, this.id, motd))
                .thenApply(data -> {
                    this.motd = data.getMotd();
                    return data;
                });
    }

    /**
     * Get player info
     *
     * @return server player info
     */
    public PlayerInfo getPlayerInfo() {
        return players;
    }

    /**
     * Get the host
     * (Only available if the server is online)
     *
     * @return host server address
     */
    public String getHost() {
        return host;
    }

    /**
     * Get the port
     * (Only available if the server is online)
     *
     * @return server port
     */
    public int getPort() {
        return port;
    }


    /**
     * Get the server software
     *
     * @return server software
     */
    public ServerSoftware getSoftware() {
        return software;
    }


    /**
     * Is the server shared
     *
     * @return server shared
     */
    public boolean isShared() {
        return shared;
    }

    /**
     * get the exaroton client used to create this server
     *
     * @return exaroton client used to create this server
     */
    public ExarotonClient getClient() {
        return client;
    }


    /**
     * Fetch the server from the API
     *
     * @return full server
     * @throws IOException connection errors
     */
    public CompletableFuture<Server> get() throws IOException {
        return client.request(new GetServerRequest(this.client, this.gson, this.id))
                .thenApply(this::setFromObject);
    }

    /**
     * Get the current server log
     *
     * @return server log
     * @throws IOException connection errors
     */
    public CompletableFuture<ServerLog> getLog() throws IOException {
        return client.request(new GetServerLogsRequest(this.client, this.gson, this.id));
    }

    /**
     * Share the server log to mclo.gs
     *
     * @return mclogs response
     * @throws IOException connection errors
     */
    public CompletableFuture<MclogsData> shareLog() throws IOException {
        return client.request(new ShareServerLogsRequest(this.client, this.gson, this.id));
    }

    /**
     * Get the server RAM
     *
     * @return ram info
     * @throws IOException connection errors
     */
    public CompletableFuture<ServerRAMInfo> getRAM() throws IOException {
        return client.request(new GetServerRAMRequest(this.client, this.gson, this.id));
    }

    /**
     * Set the sever RAM
     *
     * @param ram new RAM in GB
     * @return new ram info
     * @throws IOException connection errors
     */
    public CompletableFuture<ServerRAMInfo> setRAM(int ram) throws IOException {
        return client.request(new SetServerRAMRequest(this.client, this.gson, this.id, ram));
    }

    /**
     * Start the server. Equivalent to {@link #start(boolean)} with useOwnCredits = false.
     *
     * @throws IOException connection errors
     * @return Completable future with an updated server object. This future completes after the request, not once the server has started.
     */
    public CompletableFuture<Server> start() throws IOException {
        return this.start(false);
    }


    /**
     * Start the server
     *
     * @param useOwnCredits use the credits of the account that created the API key instead of the server owner's credits
     * @throws IOException connection errors
     * @return Completable future with an updated server object. This future completes after the request, not once the server has started.
     */
    public CompletableFuture<Server> start(boolean useOwnCredits) throws IOException {
        return client.request(new StartServerRequest(this.client, this.gson, this.id, useOwnCredits))
                .thenApply(this::setFromObject);
    }

    /**
     * Stop the server
     *
     * @throws IOException connection errors
     * @return Completable future with an updated server object. This future completes after the request, not once the server has stopped.
     */
    public CompletableFuture<Server> stop() throws IOException {
        return client.request(new StopServerRequest(this.client, this.gson, this.id))
                .thenApply(this::setFromObject);
    }

    /**
     * Restart the server
     *
     * @throws IOException connection errors
     * @return Completable future with an updated server object. This future completes after the request, not once the server has restarted.
     */
    public CompletableFuture<Server> restart() throws IOException {
        return client.request(new RestartServerRequest(this.client, this.gson, this.id))
                .thenApply(this::setFromObject);
    }

    /**
     * Execute a server command. If a websocket connection with the console stream is active the command will be sent via the websocket.
     *
     * @param command command that will be sent to the console
     * @return completable future that completes once the command has been sent
     * @throws IOException connection errors
     */
    public CompletableFuture<Void> executeCommand(String command) throws IOException {
        if (this.webSocket != null && this.webSocket.executeCommand(command)) {
            return CompletableFuture.completedFuture(null);
        }
        return client.request(new ExecuteCommandRequest(this.client, this.gson, this.id, command));
    }

    /**
     * Get a list of available player lists
     *
     * @return available player lists
     * @throws IOException connection errors
     */
    public CompletableFuture<List<String>> getPlayerLists() throws IOException {
        return client.request(new GetPlayerListsRequest(this.client, this.gson, this.id));
    }

    /**
     * Get a file. This method does not request any data of the file.
     *
     * @param path file path
     * @return empty ServerFile object
     * @see ServerFile#get()
     */
    public ServerFile getFile(String path) {
        return new ServerFile(this.client, this.gson, this, path);
    }

    /**
     * Get a player list.
     *
     * @param name player list name (see {@link #getPlayerLists()})
     * @return player list
     */
    public PlayerList getPlayerList(String name) {
        return new PlayerList(this.client, this.gson, this.id, name);
    }

    /**
     * update properties from fetched object
     *
     * @param server server fetched from the API
     * @return updated server object
     */
    @ApiStatus.Internal
    public Server setFromObject(Server server) {
        this.fetched = true;
        this.id = server.getId();
        this.name = server.getName();
        this.address = server.getAddress();
        this.motd = server.getMotd();
        this.status = server.getStatus().getValue();
        this.players = server.getPlayerInfo();
        this.host = server.getHost();
        this.port = server.getPort();
        this.software = server.getSoftware();
        this.shared = server.isShared();
        return this;
    }

    /**
     * Set the exaroton client used for requests and the gson instance. This method assumes the server is already fetched.
     *
     * @param client exaroton client used for new requests
     * @param gson   gson instance used for (de-)serialization
     */
    @ApiStatus.Internal
    @Override
    public void initialize(@NotNull ExarotonClient client, @NotNull Gson gson) {
        this.client = Objects.requireNonNull(client);
        this.gson = Objects.requireNonNull(gson);
        this.fetched = true;
    }

    /**
     * subscribe to websocket events
     */
    public void subscribe() {
        if (this.webSocket != null) {
            throw new IllegalStateException("Websocket connection already active.");
        }

        this.webSocket = client.connectToWebSocket(this, "servers/" + this.id + "/websocket");
    }

    /**
     * subscribe to one or more streams
     *
     * @param streams stream names
     */
    public void subscribe(StreamName... streams) {
        for (StreamName stream : streams) {
            if (this.webSocket == null) {
                this.subscribe();
            }

            this.webSocket.subscribe(stream);
        }
    }

    /**
     * unsubscribe from websocket events
     */
    public void unsubscribe() {
        if (this.webSocket == null) throw new RuntimeException("No websocket connection active.");
        this.webSocket.close();
        this.webSocket = null;
    }

    /**
     * unsubscribe from one or more streams
     *
     * @param streams stream names
     */
    public void unsubscribe(StreamName... streams) {
        if (this.webSocket == null) throw new RuntimeException("No websocket connection active.");
        for (StreamName stream : streams) {
            this.webSocket.unsubscribe(stream);
        }
    }

    /**
     * subscribe to server status changes
     *
     * @param subscriber status change handler
     */
    public void addStatusSubscriber(ServerStatusSubscriber subscriber) {
        if (this.webSocket == null) throw new RuntimeException("No websocket connection active.");
        this.webSocket.addServerStatusSubscriber(subscriber);
    }

    /**
     * subscribe to console messages
     *
     * @param subscriber console message handler
     */
    public void addConsoleSubscriber(ConsoleSubscriber subscriber) {
        if (this.webSocket == null) throw new RuntimeException("No websocket connection active.");
        this.webSocket.addConsoleSubscriber(subscriber);
    }

    /**
     * subscribe to heap data
     *
     * @param subscriber heap data handler
     */
    public void addHeapSubscriber(HeapSubscriber subscriber) {
        if (this.webSocket == null) throw new RuntimeException("No websocket connection active.");
        this.webSocket.addHeapSubscriber(subscriber);
    }

    /**
     * subscribe to stats
     *
     * @param subscriber stats handler
     */
    public void addStatsSubscriber(StatsSubscriber subscriber) {
        if (this.webSocket == null) throw new RuntimeException("No websocket connection active.");
        this.webSocket.addStatsSubscriber(subscriber);
    }

    /**
     * subscribe to ticks
     *
     * @param subscriber tick data handler
     */
    public void addTickSubscriber(TickSubscriber subscriber) {
        if (this.webSocket == null) throw new RuntimeException("No websocket connection active.");
        this.webSocket.addTickSubscriber(subscriber);
    }

    /**
     * @return web socket manager
     */
    public WebSocketConnection getWebSocket() {
        return webSocket;
    }
}
