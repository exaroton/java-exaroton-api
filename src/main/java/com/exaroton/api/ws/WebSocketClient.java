package com.exaroton.api.ws;

import com.google.gson.JsonParser;
import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.net.URI;

@ApiStatus.Internal
public final class WebSocketClient extends org.java_websocket.client.WebSocketClient {
    /**
     * logger
     */
    private final Logger logger;

    /**
     * WebSocketManager
     */
    private final WebSocketManager manager;

    /**
     * @param logger SLF4J logger
     * @param uri websocket uri
     * @param manager websocket manager
     */
    public WebSocketClient(URI uri, Logger logger, WebSocketManager manager) {
        super(uri);
        this.logger = logger;
        this.manager = manager;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        logger.debug("Websocket opened with status {}: {}", handshakedata.getHttpStatus(), handshakedata.getHttpStatusMessage());
        manager.handleOpen();
    }

    @Override
    public void onMessage(String message) {
        var type = JsonParser.parseString(message)
                .getAsJsonObject()
                .get("type")
                .getAsString();
        switch (type) {
            case "connected":
            case "keep-alive":
            case "disconnected":
                break;

            default:
                manager.handleData(type, message);
        }
    }

    @Override
    public void onError(Exception ex) {
        logger.error("A websocket error ocurred", ex);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.debug("Websocket closed with code {}: {}", code, reason);
        manager.handleClose(remote);
    }
}
