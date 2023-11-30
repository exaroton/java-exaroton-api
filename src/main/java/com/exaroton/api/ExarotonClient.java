package com.exaroton.api;

import com.exaroton.api.account.Account;
import com.exaroton.api.request.account.GetAccountRequest;
import com.exaroton.api.request.server.GetServersRequest;
import com.exaroton.api.server.Server;
import com.exaroton.api.server.config.ConfigOption;
import com.exaroton.api.server.config.ConfigOptionTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.stream.Collectors;

public class ExarotonClient {
    /**
     * Gson instance used for (de-)serialization
     */
    private final Gson gson;

    /**
     * Request protocol
     */
    private String protocol = "https";

    /**
     * API host
     */
    private final String host = "api.exaroton.com";

    /**
     * API base path
     */
    private final String basePath = "/v1/";

    /**
     * API user agent
     */
    private String userAgent = "java-exaroton-api@1.2.1";

    /**
     * exaroton API token
     */
    private String apiToken;

    /**
     * @param apiToken exaroton API token
     */
    public ExarotonClient(String apiToken) {
        this.apiToken = apiToken;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(ConfigOption.class, new ConfigOptionTypeAdapter())
                .create();
    }

    public Gson getGson() {
        return this.gson;
    }


    /**
     * update the API token
     * @param apiToken exaroton API token
     * @return the updated client
     */
    public ExarotonClient setAPIToken(String apiToken){
        if (apiToken == null || apiToken.length() == 0) {
            throw new IllegalArgumentException("No API token specified");
        }

        this.apiToken = apiToken;
        return this;
    }

    /**
     * update the user agent
     * @param userAgent user agent
     * @return the updated client
     */
    public ExarotonClient setUserAgent(String userAgent) {
        if (userAgent == null || userAgent.length() == 0) {
            throw new IllegalArgumentException("No user agent specified");
        }

        this.userAgent = userAgent;
        return this;
    }

    /**
     * get the base URL
     * @return base URL for api requests
     */
    protected String baseURL(){
        return protocol + "://" + host + basePath;
    }

    /**
     * @return request protocol
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * @return API host
     */
    public String getHost() {
        return host;
    }

    /**
     * @return API base path
     */
    public String getBasePath() {
        return basePath;
    }

    /**
     * @return API token
     */
    public String getApiToken() {
        return apiToken;
    }

    /**
     * Change the protocol used for API requests
     * Supported values: http, https
     * @param protocol the new protocol
     * @throws UnsupportedProtocolException protocol is not supported
     * @return the updated client
     */
    public ExarotonClient setProtocol(String protocol) throws UnsupportedProtocolException {
        if (protocol == null) throw new IllegalArgumentException("No protocol specified");

        switch (protocol.toLowerCase(Locale.ROOT)) {
            case "http":
                this.protocol = "http";
                break;
            case "https":
                this.protocol = "https";
                break;
            default:
                throw new UnsupportedProtocolException(protocol + " is not a supported protocol");
        }
        return this;
    }

    /**
     * @param method HTTP method
     * @param endpoint api endpoint
     * @return http connection with user agent and authorization
     * @throws IOException failed to open connection
     */
    public HttpURLConnection createConnection(String method, String endpoint) throws IOException {
        URL url = new URL(this.baseURL() + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("User-Agent", this.userAgent);
        connection.setRequestProperty("Authorization", "Bearer " + this.apiToken);
        return connection;
    }

    /**
     * make a JSON request to the exaroton API
     * @param endpoint API endpoint e.g. "account/"
     * @param method HTTP method e.g. GET or POST
     * @return content string
     * @throws APIException connection and API errors
     * @deprecated use {{@link APIRequest}} instead
     */
    @Deprecated
    public String request(String endpoint, String method) throws APIException {
        HttpURLConnection connection = null;
        InputStream stream;
        try {
            connection = this.createConnection(method, endpoint);
            connection.setRequestProperty("Content-Type", "application/json");

            stream = connection.getInputStream();
        }
        catch (IOException e) {
            if (connection == null || connection.getErrorStream() == null) {
                throw new APIException("Failed to request data from exaroton API", e);
            }

            stream = connection.getErrorStream();
        }

        return new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
    }

    /**
     * get the account that owns the api key
     * @return account that owns the api key
     * @throws APIException connection and API errors
     */
    public Account getAccount() throws APIException {
        GetAccountRequest request = new GetAccountRequest(this);
        return request.request().getData();
    }

    /**
     * list all servers you have access to
     * @return accessible servers
     * @throws APIException connection and API errors
     */
    public Server[] getServers() throws APIException {
        GetServersRequest request = new GetServersRequest(this);
        Server[] servers = request.request().getData();
        for (Server server: servers) {
            server.setClient(this);
            server.fetched = true;
        }
        return servers;
    }

    /**
     * get a server
     * @param id server id
     * @return empty server object
     */
    public Server getServer(String id) {
        return new Server(this, id);
    }

    /**
     * Get the current exaroton server using the EXAROTON_SERVER_ID environment variable.
     * If the environment variable is not set returns null
     * @return the exaroton server running this code
     */
    public Server getCurrentServer() {
        String id = System.getenv("EXAROTON_SERVER_ID");
        if (id == null) return null;
        return this.getServer(id);
    }
}
