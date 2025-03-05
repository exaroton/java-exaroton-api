package com.exaroton.api.server;

import com.exaroton.api.APIException;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.request.server.*;
import com.exaroton.api.ws.WebSocketManager;
import com.exaroton.api.ws.subscriber.*;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Server {

    /**
     * has this server been fetched from the API yet
     */
    public boolean fetched;

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
    private transient WebSocketManager webSocket;

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
    public Server(@NotNull ExarotonClient client, @NotNull Gson gson, @NotNull String id) {
        this.fetched = false;
        this.client = Objects.requireNonNull(client);
        this.gson = Objects.requireNonNull(gson);
        this.id = Objects.requireNonNull(id);
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
     * see ServerStatus
     *
     * @return status code (see ServerStatus)
     */
    public int getStatus() {
        return status;
    }

    /**
     * check if the server has this status
     *
     * @param statusCodes status codes (see {@link ServerStatus})
     * @return status match
     */
    public boolean hasStatus(int... statusCodes) {
        if (statusCodes == null) throw new IllegalArgumentException("Invalid status code array");
        for (int statusCode : statusCodes) {
            if (this.status == statusCode) return true;
        }
        return false;
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
     * @throws APIException connection or API errors
     */
    public ServerMOTDInfo fetchMotd() throws APIException {
        GetServerMOTDRequest request = new GetServerMOTDRequest(this.client, this.gson, this.id);
        ServerMOTDInfo motd = request.request().getData();
        this.motd = motd.getMotd();
        return motd;
    }

    /**
     * Set the server MOTD
     *
     * @param motd new server MOTD
     * @return updated server MOTD
     * @throws APIException connection or API errors
     */
    public ServerMOTDInfo setMotd(String motd) throws APIException {
        SetServerMOTDRequest request = new SetServerMOTDRequest(this.client, this.gson, this.id, motd);
        return request.request().getData();
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
     * @throws APIException connection or API errors
     */
    public Server get() throws APIException {
        this.fetched = true;
        GetServerRequest request = new GetServerRequest(this.client, this.gson, this.id);
        this.setFromObject(request.request().getData());
        return this;
    }

    /**
     * Get the current server log
     *
     * @return server log
     * @throws APIException connection or API errors
     */
    public ServerLog getLog() throws APIException {
        GetServerLogsRequest request = new GetServerLogsRequest(this.client, this.gson, this.id);
        return request.request().getData();
    }

    /**
     * Share the server log to mclo.gs
     *
     * @return mclogs response
     * @throws APIException connection or API errors
     */
    public MclogsData shareLog() throws APIException {
        ShareServerLogsRequest request = new ShareServerLogsRequest(this.client, this.gson, this.id);
        return request.request().getData();
    }

    /**
     * Get the server RAM
     *
     * @return ram info
     * @throws APIException connection or API errors
     */
    public ServerRAMInfo getRAM() throws APIException {
        GetServerRAMRequest request = new GetServerRAMRequest(this.client, this.gson, this.id);
        return request.request().getData();
    }

    /**
     * Set the sever RAM
     *
     * @param ram new RAM in GB
     * @return new ram info
     * @throws APIException connection or API errors
     */
    public ServerRAMInfo setRAM(int ram) throws APIException {
        SetServerRAMRequest request = new SetServerRAMRequest(this.client, this.gson, this.id, ram);
        return request.request().getData();
    }

    /**
     * Start the server
     * Equivalent to {@link #start(boolean)} with useOwnCredits = false
     *
     * @throws APIException connection or API errors
     */
    public void start() throws APIException {
        this.start(false);
    }


    /**
     * Start the server
     *
     * @param useOwnCredits use the credits of the account that created the API key instead of the server owner's credits
     * @throws APIException connection or API errors
     */
    public void start(boolean useOwnCredits) throws APIException {
        StartServerRequest request = new StartServerRequest(this.client, this.gson, this.id, useOwnCredits);
        request.request();
    }

    /**
     * Stop the server
     *
     * @throws APIException connection or API errors
     */
    public void stop() throws APIException {
        StopServerRequest request = new StopServerRequest(this.client, this.gson, this.id);
        request.request();
    }

    /**
     * Restart the server
     *
     * @throws APIException connection or API errors
     */
    public void restart() throws APIException {
        RestartServerRequest request = new RestartServerRequest(this.client, this.gson, this.id);
        request.request();
    }

    /**
     * Execute a server command
     *
     * @param command command that will be sent to the console
     * @throws APIException connection or API errors
     */
    public void executeCommand(String command) throws APIException {
        if (this.webSocket == null || !this.webSocket.executeCommand(command)) {
            ExecuteCommandRequest request = new ExecuteCommandRequest(this.client, this.gson, this.id, command);
            request.request();
        }
    }

    /**
     * Get a list of available player lists
     *
     * @return available player lists
     * @throws APIException connection or API errors
     */
    public String[] getPlayerLists() throws APIException {
        GetPlayerListsRequest request = new GetPlayerListsRequest(this.client, this.gson, this.id);
        return request.request().getData();
    }

    /**
     * get a file
     *
     * @param path file path
     * @return octet stream
     */
    public ServerFile getFile(String path) {
        return new ServerFile(this.client, this.gson, this, path);
    }

    /**
     * get a player list
     *
     * @param name player list name (see getPlayerLists())
     * @return empty player list
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
    public Server setFromObject(Server server) {
        this.id = server.getId();
        this.name = server.getName();
        this.address = server.getAddress();
        this.motd = server.getMotd();
        this.status = server.getStatus();
        this.players = server.getPlayerInfo();
        this.host = server.getHost();
        this.port = server.getPort();
        this.software = server.getSoftware();
        this.shared = server.isShared();
        return this;
    }

    /**
     * set the exaroton client used for requests and the gson instance
     *
     * @param client exaroton client used for new requests
     * @param gson   gson instance used for (de-)serialization
     */
    public void init(@NotNull ExarotonClient client, @NotNull Gson gson) {
        this.client = Objects.requireNonNull(client);
        this.gson = Objects.requireNonNull(gson);
    }

    /**
     * subscribe to websocket events
     */
    public void subscribe() {
        String protocol = this.client.getProtocol().equals("https") ? "wss" : "ws";
        String uri = protocol + "://" + this.client.getHost() + this.client.getBasePath() + "servers/" + this.id + "/websocket";
        this.webSocket = new WebSocketManager(client, gson, uri, this.client.getApiToken(), this);
    }

    /**
     * subscribe to one or more streams
     *
     * @param streams stream names
     */
    public void subscribe(String... streams) {
        for (String stream : streams) {
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
    public void unsubscribe(String... streams) {
        if (this.webSocket == null) throw new RuntimeException("No websocket connection active.");
        for (String stream : streams) {
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
    public WebSocketManager getWebSocket() {
        return webSocket;
    }
}
