package com.exaroton.api;


import com.google.gson.Gson;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class APIRequest<Datatype> {
    /**
     * exaroton API client
     */
    protected final ExarotonClient client;

    public APIRequest(ExarotonClient client) {
        this.client = client;
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
     * @return request url with parameters
     * @throws APIException failed to encode URL parameters
     */
    protected String getUrl() throws APIException {
        String endpoint = this.getEndpoint();
        //replace data
        for (Map.Entry<String, String> entry : this.getData().entrySet()) {
            endpoint = endpoint.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        //add parameters
        boolean first = true;
        StringBuilder url = new StringBuilder(endpoint);
        for (Parameter parameter: this.getParameters()) {
            if (first) {
                url.append("?");
                first = false;
            }
            else {
                url.append("&");
            }
            try {
                url.append(parameter.getName())
                        .append("=")
                        .append(URLEncoder.encode(parameter.getValue(), StandardCharsets.UTF_8.toString()));
            } catch (UnsupportedEncodingException e) {
                throw new APIException("Error encoding URL parameters", e);
            }
        }
        return url.toString();
    }

    public InputStream requestRaw() throws APIException {
        HttpURLConnection connection = null;
        InputStream stream;
        try {
            connection = client.createConnection(this.getMethod(), this.getUrl());
            for (Map.Entry<String, String> entry : this.getHeaders().entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }

            InputStream inputStream = this.getInputStream();
            if (this.getInputStream() != null) {
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

    public APIResponse<Datatype> request() throws APIException {
        String json = this.requestString();
        APIResponse<Datatype> response = (new Gson()).fromJson(json, this.getType());
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
     * List of request parameters
     * @return http parameters
     */
    protected ArrayList<Parameter> getParameters() {
        return new ArrayList<>();
    }

    /**
     * @return input stream with data that should be sent to the request
     */
    protected InputStream getInputStream() {
        return null;
    }
}