package com.exaroton.api.ws;

import com.exaroton.api.ExarotonClient;
import com.google.gson.Gson;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class WebSocketClient extends org.java_websocket.client.WebSocketClient {
    /**
     * logger
     */
    private final Logger logger =  LoggerFactory.getLogger("java-exaroton-api");

    /**
     * WebSocketManager
     */
    private final WebSocketManager manager;

    /**
     * Gson instance for (de-)serialization
     */
    private final Gson gson;

    /**
     * @param uri websocket uri
     * @param manager websocket manager
     */
    public WebSocketClient(URI uri, WebSocketManager manager, Gson gson) {
        super(uri);
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        manager.sendDebug("Websocket opened with status " + handshakedata.getHttpStatus() + ": " + handshakedata.getHttpStatusMessage());
        manager.handleOpen();
    }

    @Override
    public void onMessage(String message) {
        WSMessage m = gson.fromJson(message, WSMessage.class);
        switch (m.getType()) {

            case "connected":
            case "keep-alive":
            case "disconnected":
                break;

            default:
                manager.handleData(m.getType(), message);
        }
    }

    @Override
    public void onError(Exception ex) {
        logger.error("A websocket error ocurred", ex);
        manager.onError("A websocket error ocurred: " + ex.getMessage(), ex);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        manager.sendDebug("Websocket closed with code " + code + ": " + reason);
        manager.handleClose(code, reason, remote);
    }
}
