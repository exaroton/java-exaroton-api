package com.exaroton.api;


import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    public APIResponse<Datatype> request() throws APIException {
        StringBuilder endpoint = new StringBuilder(this.getEndpoint());

        //replace data
        for (Map.Entry<String, String> entry : this.getData().entrySet()) {
            endpoint = new StringBuilder(endpoint.toString().replace("{" + entry.getKey() + "}", entry.getValue()));
        }

        //add parameters
        boolean first = true;
        for (Parameter parameter: this.getParameters()) {
            if (first) {
                endpoint.append("?");
                first = false;
            }
            else {
                endpoint.append("&");
            }
            endpoint.append(parameter.getName())
                    .append("=")
                    .append(parameter.getValue());
        }

        String json = client.request(endpoint.toString(), this.getMethod());
        APIResponse<Datatype> response = (new Gson()).fromJson(json, this.getType());
        if (!response.isSuccess()) throw new APIException(response.getError());

        return response;
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
}