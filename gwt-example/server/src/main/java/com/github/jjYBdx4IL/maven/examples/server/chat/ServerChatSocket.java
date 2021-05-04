package com.github.jjYBdx4IL.maven.examples.server.chat;

import com.google.gwt.user.client.rpc.SerializationException;
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
    private Session session = null;

    public ServerChatSocket(ChatServer chatServer) {
        LOG.info("ServerChatSocket instance created with chat server instance " + chatServer);
        this.chatServer = chatServer;
    }

    @Override
    public void onWebSocketConnect(Session sess) {
        super.onWebSocketConnect(sess);
        chatServer.add(Message.createConnect(sess));
        LOG.info("Socket Connected: " + sess);
        this.session = sess;
    }

    @Override
    public void onWebSocketText(String message) {
        super.onWebSocketText(message);
        LOG.info("Received TEXT message: " + message);
        try {
            chatServer.add(Message.createMsg(GWTSerializationUtils.deserializeMessage(message), this.session));
        } catch (SerializationException ex) {
            LOG.error("failed to deserialize message >>>" + message + "<<<", ex);
        }
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);
        LOG.info("Socket Closed: [" + statusCode + "] " + reason);
        chatServer.add(Message.createDisonnect(this.session));
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        super.onWebSocketError(cause);
        LOG.error("", cause);
        chatServer.add(Message.createDisonnect(this.session));
    }

}
