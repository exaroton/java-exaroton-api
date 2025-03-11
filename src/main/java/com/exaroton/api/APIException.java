package com.exaroton.api;

/**
 * Exception thrown by the exaroton API.
 */
public class APIException extends Exception {
    public APIException(String message) {
        super(message);
    }
}
