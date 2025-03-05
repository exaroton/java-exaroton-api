package com.exaroton.api;


import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class APIRequest<Datatype> {
    /**
     * exaroton API client
     */
    protected final ExarotonClient client;

    /**
     * Gson instance used for (de-)serialization
     */
    protected final Gson gson;

    public APIRequest(@NotNull ExarotonClient client, @NotNull Gson gson) {
        this.client = Objects.requireNonNull(client);
        this.gson = Objects.requireNonNull(gson);
    }

    /**
     * get API endpoint
     *
     * @return API endpoint
     */
    protected abstract String getEndpoint();

    /**
     * get request method
     *
     * @return request method
     */
    protected String getMethod() {
        return "GET";
    }

    /**
     * @return request path with replaced parameters
     */
    protected String getPath() {
        String path = this.getEndpoint();

        for (Map.Entry<String, String> entry : this.getData().entrySet()) {
            if (entry.getValue() == null) {
                throw new IllegalStateException("Path variable " + entry.getKey() + " can't be null");
            }

            path = path.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        return path;
    }

    /**
     * Execute this API Request and get the raw InputStream
     * @return InputStream
     * @throws APIException if the request fails
     */
    public InputStream requestRaw() throws APIException {
        HttpURLConnection connection = null;
        InputStream stream;
        try {
            connection = client.createConnection(this.getMethod(), this.getPath());
            for (Map.Entry<String, String> entry : this.getHeaders().entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }

            Object body = this.getBody();
            InputStream inputStream = this.getInputStream();
            if (body != null) {
                inputStream = new ByteArrayInputStream(gson.toJson(body).getBytes(StandardCharsets.UTF_8));
                connection.setRequestProperty("Content-Type", "application/json");
            }

            if (inputStream != null) {
                connection.setDoOutput(true);
                OutputStream out = connection.getOutputStream();
                byte[] buf = new byte[8192];
                int length;
                while ((length = inputStream.read(buf)) > 0) {
                    out.write(buf, 0, length);
                }
            }
            stream = connection.getInputStream();
        }
        catch (IOException e) {
            if (connection == null || connection.getErrorStream() == null) {
                throw new APIException("Failed to request data from exaroton API", e);
            }

            stream = connection.getErrorStream();
        }

        return stream;
    }

    /**
     * Execute this API Request and get the response as a String
     * @return response as a String
     * @throws APIException if the request fails
     */
    public String requestString() throws APIException {
        try (InputStream stream = this.requestRaw()) {
            return new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
        }
        catch (IOException e) {
            throw new APIException("Failed to read input stream", e);
        }
    }

    /**
     * Execute this API Request and parse the API response
     * @return Parsed API response
     * @throws APIException if the request fails
     */
    public APIResponse<Datatype> request() throws APIException {
        String json = this.requestString();
        APIResponse<Datatype> response = gson.fromJson(json, this.getType());
        if (!response.isSuccess()) throw new APIException(response.getError());

        return response;
    }

    /**
     * @return request headers
     */
    protected HashMap<String, String> getHeaders() {
        HashMap<String, String> map = new HashMap<>();
        map.put("Response-Type", "application/json");
        return map;
    }

    /**
     * get the type required for parsing the JSON response
     * @return response type
     */
    protected abstract Type getType();

    /**
     * data that will be replaced in the endpoint
     * @return data
     */
    protected HashMap<String, String> getData() {
        return new HashMap<>();
    }

    /**
     * @return input stream with data that should be sent to the request
     */
    protected InputStream getInputStream() {
        return null;
    }

    /**
     * Get the request body
     * @return request body
     */
    protected Object getBody() {
        return null;
    }
}
