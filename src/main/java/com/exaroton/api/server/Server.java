package com.exaroton.api.server;

import com.exaroton.api.APIException;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.request.server.*;

public class Server {

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
    private PlayerInfo playerInfo;

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
    private ExarotonClient client;

    public Server(ExarotonClient client, String id) {
        this.client = client;
        this.id = id;
    }

    /**
     * Get the server id
     * @return unique server id (tgkm731xO7GiHt76)
     */
    public String getId() {
        return id;
    }

    /**
     * Get the server name
     * @return unique server name (example)
     */
    public String getName() {
        return name;
    }

    /**
     * Get the server address
     * @return unique server address (example.exaroton.com)
     */
    public String getAddress() {
        return address;
    }

    /**
     * Get the current server status
     * see ServerStatus
     * @return status code (see ServerStatus)
     */
    public int getStatus() {
        return status;
    }

    /**
     * check if the server has this status
     * @param status status code (see ServerStatus)
     * @return status match
     */
    public boolean hasStatus(int status) {
        return this.status == status;
    }

    /**
     * check if the server has one of these status codes
     * @param statusCodes status codes (see ServerStatus)
     * @return status match
     */
    public boolean hasStatus(int[] statusCodes) {
        if (statusCodes == null) throw new IllegalArgumentException("Invalid status code array");
        for (int statusCode: statusCodes) {
            if (this.status == statusCode) return true;
        }
        return false;
    }

    /**
     * Get the MOTD (message of the day)
     * @return server MOTD
     */
    public String getMotd() {
        return motd;
    }

    /**
     * Get player info
     * @return server player info
     */
    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }

    /**
     * Get the host
     * (Only available if the server is online)
     * @return host server address
     */
    public String getHost() {
        return host;
    }

    /**
     * Get the port
     * (Only available if the server is online)
     * @return server port
     */
    public int getPort() {
        return port;
    }


    /**
     * Get the server software
     * @return server software
     */
    public ServerSoftware getSoftware() {
        return software;
    }


    /**
     * Is the server shared
     * @return server shared
     */
    public boolean isShared() {
        return shared;
    }


    /**
     * Fetch the server from the API
     * @return full server
     * @throws APIException connection or API errors
     */
    public Server get() throws APIException {
        GetServerRequest request = new GetServerRequest(this.client, this.id);
        this.setFromObject(request.request().getData());
        return this;
    }

    /**
     * Get the current server log
     * @return server log
     * @throws APIException connection or API errors
     */
    public ServerLog getLog() throws APIException {
        GetServerLogsRequest request = new GetServerLogsRequest(this.client, this.id);
        return request.request().getData();
    }

    /**
     * Share the server log to mclo.gs
     * @return mclogs response
     * @throws APIException connection or API errors
     */
    public MclogsData shareLog() throws APIException {
        ShareServerLogsRequest request = new ShareServerLogsRequest(this.client, this.id);
        return request.request().getData();
    }

    /**
     * Get the server RAM
     * @return ram info
     * @throws APIException connection or API errors
     */
    public ServerRAMInfo getRAM() throws APIException {
        GetServerRAMRequest request = new GetServerRAMRequest(this.client, this.id);
        return request.request().getData();
    }

    /**
     * Set the sever RAM
     * @param ram new RAM in GB
     * @return new ram info
     * @throws APIException connection or API errors
     */
    public ServerRAMInfo setRAM(int ram) throws APIException {
        SetServerRAMRequest request = new SetServerRAMRequest(this.client, this.id, ram);
        return request.request().getData();
    }

    /**
     * Start the server
     * @throws APIException connection or API errors
     */
    public void start() throws APIException {
        StartServerRequest request = new StartServerRequest(this.client, this.id);
        request.request();
    }

    /**
     * Stop the server
     * @throws APIException connection or API errors
     */
    public void stop() throws APIException {
        StartServerRequest request = new StartServerRequest(this.client, this.id);
        request.request();
    }

    /**
     * Restart the server
     * @throws APIException connection or API errors
     */
    public void restart() throws APIException {
        StartServerRequest request = new StartServerRequest(this.client, this.id);
        request.request();
    }

    /**
     * Execute a server command
     * @param command command that will be sent to the console
     * @throws APIException connection or API errors
     */
    public void executeCommand(String command) throws APIException {
        ExecuteCommandRequest request = new ExecuteCommandRequest(this.client, this.id, command);
        request.request();
    }

    /**
     * Get a list of available player lists
     * @return available player lists
     * @throws APIException connection or API errors
     */
    public String[] getPlayerLists() throws APIException {
        GetPlayerListsRequest request = new GetPlayerListsRequest(this.client, this.id);
        return request.request().getData();
    }

    /**
     * get a player list
     * @param name player list name (see getPlayerLists())
     * @return empty player list
     */
    public PlayerList getPlayerList(String name) {
        return new PlayerList(name, this.id, this.client);
    }

    /**
     * update properties from fetched object
     * @param server server fetched from the API
     */
    private void setFromObject(Server server) {
        this.id = server.getId();
        this.name = server.getName();
        this.address = server.getAddress();
        this.motd = server.getMotd();
        this.status = server.getStatus();
        this.playerInfo = server.getPlayerInfo();
        this.host = server.getHost();
        this.port = server.getPort();
        this.software = server.getSoftware();
        this.shared = server.isShared();
    }

    /**
     * set the exaroton client used for requests
     * @param client exaroton client used for new requests
     */
    public void setClient(ExarotonClient client) {
        if (client == null) throw new IllegalArgumentException("No client provided");
        this.client = client;
    }
}
