package com.github.jjYBdx4IL.maven.examples.gwt.server.chat;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class ServerChatSocket extends WebSocketAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(ServerChatSocket.class);

    private final ChatServer chatServer;

    public ServerChatSocket(ChatServer chatServer) {
        LOG.info("ServerChatSocket instance created with chat server instance " + chatServer);
        this.chatServer = chatServer;
    }

    @Override
    public void onWebSocketConnect(Session sess) {
        super.onWebSocketConnect(sess);
        chatServer.add(Message.createConnect(sess));
        LOG.info("Socket Connected: " + sess);
    }

    @Override
    public void onWebSocketText(String message) {
        super.onWebSocketText(message);
        LOG.info("Received TEXT message: " + message);
        chatServer.add(Message.createMsg(message));
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);
        LOG.info("Socket Closed: [" + statusCode + "] " + reason);
        chatServer.add(Message.createDisonnect(getSession()));
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        super.onWebSocketError(cause);
        LOG.error("", cause);
        chatServer.add(Message.createDisonnect(getSession()));
    }

}
