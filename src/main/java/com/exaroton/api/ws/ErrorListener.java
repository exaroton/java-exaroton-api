package com.exaroton.api.ws;

public interface ErrorListener {
    void onError(String error, Throwable t);
}