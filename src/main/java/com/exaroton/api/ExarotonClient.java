package com.exaroton.api;

import com.exaroton.api.account.Account;
import com.exaroton.api.billing.pools.CreditPool;
import com.exaroton.api.request.account.GetAccountRequest;
import com.exaroton.api.request.billing.pools.GetCreditPoolsRequest;
import com.exaroton.api.request.server.GetServersRequest;
import com.exaroton.api.server.Server;
import com.exaroton.api.server.config.ConfigOptionTypeAdapterFactory;
import com.exaroton.api.ws.WebSocketConnection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ExarotonClient {
    /**
     * HTTP client
     */
    private final HttpClient httpClient;

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
        this.httpClient = HttpClient.newBuilder().build();
        this.apiToken = apiToken;
        this.gson = new GsonBuilder()
                .registerTypeAdapterFactory(new ConfigOptionTypeAdapterFactory())
                .create();
    }

    /**
     * update the API token
     *
     * @param apiToken exaroton API token
     * @return the updated client
     */
    public ExarotonClient setAPIToken(String apiToken) {
        if (apiToken == null || apiToken.isEmpty()) {
            throw new IllegalArgumentException("No API token specified");
        }

        this.apiToken = apiToken;
        return this;
    }

    /**
     * update the user agent
     *
     * @param userAgent user agent
     * @return the updated client
     */
    public ExarotonClient setUserAgent(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            throw new IllegalArgumentException("No user agent specified");
        }

        this.userAgent = userAgent;
        return this;
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
     * Change the protocol used for API requests
     * Supported values: http, https
     *
     * @param protocol the new protocol
     * @return the updated client
     * @throws UnsupportedProtocolException protocol is not supported
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

    protected URL baseUrl() throws MalformedURLException {
        return new URL(protocol, host, basePath);
    }

    /**
     * Send an API request
     *
     * @param request     API request
     * @param bodyHandler response body handler
     * @param <T>         response type
     * @return CompletableFuture with the API response
     * @throws IOException If an error occurs while sending the request
     */
    public <T> CompletableFuture<T> request(
            @NotNull APIRequest<?> request,
            @NotNull HttpResponse.BodyHandler<T> bodyHandler
    ) throws IOException {
        Objects.requireNonNull(request);
        Objects.requireNonNull(bodyHandler);

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .header("User-Agent", userAgent)
                .header("Authorization", "Bearer " + apiToken);

        try {
            HttpRequest httpRequest = request.build(gson, builder, baseUrl());
            return httpClient.sendAsync(httpRequest, bodyHandler).thenCompose(response -> {
                var body = response.body();

                if (response.statusCode() < 200 || response.statusCode() >= 300) {
                    return CompletableFuture.failedFuture(new APIException("Failed to request data from exaroton API: "
                            + response.statusCode() + " " + response.body()));
                }

                return CompletableFuture.completedFuture(body);
            });
        } catch (URISyntaxException e) {
            throw new IOException("Failed to build request URI", e);
        }
    }

    /**
     * Send an API request that returns an APIResponse
     *
     * @param request API request
     * @param <T>     type of the data field in the API response
     * @return CompletableFuture with the API response
     * @throws IOException If an error occurs while sending the request
     */
    public <T> CompletableFuture<T> request(@NotNull APIRequest<T> request) throws IOException {
        return request(request, APIResponse.bodyHandler(this, gson, request.getType()))
                .thenApply(APIResponse::getData);
    }

    /**
     * get the account that owns the api key
     *
     * @return account that owns the api key
     * @throws IOException Connection errors
     */
    public CompletableFuture<Account> getAccount() throws IOException {
        return request(new GetAccountRequest());
    }

    /**
     * list all servers you have access to
     *
     * @return accessible servers
     * @throws IOException Connection errors
     */
    public CompletableFuture<List<Server>> getServers() throws IOException {
        return request(new GetServersRequest());
    }

    /**
     * get a server
     *
     * @param id server id
     * @return empty server object
     */
    public Server getServer(String id) {
        return new Server(this, gson, id);
    }

    /**
     * list all credit pools you have access to
     *
     * @return accessible credit pools
     * @throws IOException Connection errors
     */
    public CompletableFuture<List<CreditPool>> getCreditPools() throws IOException {
        return request(new GetCreditPoolsRequest());
    }

    /**
     * Get a credit pool object. This method does not fetch the credit pool from the API.
     *
     * @param id credit pool id
     * @return empty credit pool object
     * @see CreditPool#get()
     */
    public CreditPool getCreditPool(String id) {
        return new CreditPool(this, id);
    }

    /**
     * Get the current exaroton server using the EXAROTON_SERVER_ID environment variable. If the environment variable
     * is not set returns null. This method does not fetch the server from the API.
     *
     * @return the exaroton server running this code
     * @see Server#get()
     */
    public Server getCurrentServer() {
        String id = System.getenv("EXAROTON_SERVER_ID");
        if (id == null) return null;
        return this.getServer(id);
    }

    /**
     * Create a new websocket connection
     * @param server server to connect to
     * @param path websocket path
     * @return websocket manager
     */
    @ApiStatus.Internal
    public WebSocketConnection connectToWebSocket(Server server, String path) {
        String protocol = this.getProtocol().equals("http") ? "ws" : "wss";
        try {
            URL url = new URL(protocol, getHost(), getBasePath());
            return new WebSocketConnection(httpClient, gson, url.toURI().resolve(path), apiToken, server);
        } catch (URISyntaxException | MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
