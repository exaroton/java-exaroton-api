package com.exaroton.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.Map;

public abstract class APIRequest<Response> {
    /**
     * Build the HttpRequest
     * @param gson gson instance
     * @param builder HttpRequest builder with preconfigured options
     * @param baseUrl base URL
     * @return HttpRequest
     * @throws URISyntaxException if the constructed URI is invalid
     */
    public HttpRequest build(Gson gson, HttpRequest.Builder builder, URL baseUrl) throws URISyntaxException {
        builder.uri(baseUrl.toURI().resolve(getPath()))
                .method(this.getMethod(), getBodyPublisher(gson, builder));

        for (Map.Entry<String, String> header : this.getHeaders().entrySet()) {
            builder.header(header.getKey(), header.getValue());
        }

        return builder.build();
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
    protected abstract TypeToken<APIResponse<Response>> getType();

    /**
     * data that will be replaced in the endpoint
     * @return data
     */
    protected HashMap<String, String> getData() {
        return new HashMap<>();
    }

    /**
     * Get the body publisher for the request
     * @param gson gson instance
     * @param builder request builder to set the Content-Type header
     * @param body request body
     * @return a body publisher
     */
    protected HttpRequest.BodyPublisher jsonBodyPublisher(Gson gson, HttpRequest.Builder builder, Object body) {
        builder.header("Content-Type", "application/json");
        return HttpRequest.BodyPublishers.ofString(gson.toJson(body));
    }

    /**
     * Get the body publisher for the request
     * @param gson gson instance
     * @param builder request builder which can be used to set a Content-Type header
     * @return a body publisher
     */
    protected HttpRequest.BodyPublisher getBodyPublisher(Gson gson, HttpRequest.Builder builder) {
        return HttpRequest.BodyPublishers.noBody();
    }
}
