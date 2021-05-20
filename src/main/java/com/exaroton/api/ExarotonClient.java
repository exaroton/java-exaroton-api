package com.exaroton.api;

import com.exaroton.api.account.Account;
import com.exaroton.api.request.account.GetAccountRequest;
import com.exaroton.api.request.server.GetServersRequest;
import com.exaroton.api.server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.stream.Collectors;

public class ExarotonClient {

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
    private String userAgent = "java-exaroton-api@1.0.0";

    /**
     * exaroton API token
     */
    private String apiToken;

    /**
     * @param apiToken exaroton API token
     */
    public ExarotonClient(String apiToken) {
        this.apiToken = apiToken;
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
    private String baseURL(){
        return protocol + "://" + host + basePath;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public String getBasePath() {
        return basePath;
    }

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
     * make a request to the exaroton API
     * @param endpoint API endpoint e.g. "account/"
     * @param method HTTP method e.g. GET or POST
     * @return content string
     * @throws APIException connection and API errors
     */
    protected String request(String endpoint, String method) throws APIException {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(this.baseURL() + endpoint);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("User-Agent", userAgent);
            connection.setRequestProperty("Authorization", "Bearer " + this.apiToken);

            return new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
        }
        catch (IOException e) {
            if (connection == null || connection.getErrorStream() == null) throw new APIException("Connection problem",e);

            return new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        }
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
}
