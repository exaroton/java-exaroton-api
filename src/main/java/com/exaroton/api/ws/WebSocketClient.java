package com.exaroton.api.ws;

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
     * @param uri websocket uri
     */
    public WebSocketClient(URI uri, WebSocketManager manager) {
        super(uri);
        this.manager = manager;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        manager.handleOpen();
    }

    @Override
    public void onMessage(String message) {
        WSMessage m = (new Gson()).fromJson(message, WSMessage.class);
        switch (m.getType()) {
            case "keep-alive":
            case "connected":
            case "disconnected":
                break;

            default:
                manager.handleData(m.getType(), message);
        }
    }

    @Override
    public void onError(Exception ex) {
        logger.error("A websocket error ocurred", ex);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        manager.handleClose(code, reason, remote);
    }
}
