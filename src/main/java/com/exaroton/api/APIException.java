package com.exaroton.api;

import org.jetbrains.annotations.ApiStatus;

/**
 * Exception thrown by the exaroton API.
 */
@ApiStatus.NonExtendable
public class APIException extends Exception {
    public APIException(String message) {
        super(message);
    }
}
