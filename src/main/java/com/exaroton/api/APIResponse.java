package com.exaroton.api;

public class APIResponse<Datatype> {

    /**
     * Request success
     */
    private final boolean success;

    /**
     * Error message
     */
    private final String error;

    /**
     * Response data
     */
    private final Datatype data;

    /**
     * create an APIResponse
     * @param success request success
     * @param error error message
     * @param data response data
     */
    public APIResponse(boolean success, String error, Datatype data) {
        this.success = success;
        this.error = error;
        this.data = data;
    }


    /**
     * @return request success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @return error message
     */
    public String getError() {
        return error;
    }

    /**
     * @return response data
     */
    public Datatype getData() {
        return data;
    }
}
