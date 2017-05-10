package com.github.jjYBdx4IL.maven.examples.gwt.server.chat;

import org.eclipse.jetty.websocket.api.Session;

/**
 *
 * @author jjYBdx4IL
 */
public class Message {

    private final MessageType messageType;
    private final String message;
    private final Session session;

    public Message(MessageType messageType, String message, Session session) {
        this.messageType = messageType;
        this.message = message;
        this.session = session;
        switch (messageType) {
            case MSG:
                if (message == null) {
                    throw new NullPointerException();
                }
                break;
            case CONNECT:
            case DISCONNECT:
                if (session == null) {
                    throw new NullPointerException();
                }
                break;
        }
    }
    
    public static Message createMsg(String message) {
        return new Message(MessageType.MSG, message, null);
    }
    
    public static Message createConnect(Session session) {
        return new Message(MessageType.CONNECT, null, session);
    }

    public static Message createDisonnect(Session session) {
        return new Message(MessageType.DISCONNECT, null, session);
    }
    
    /**
     * Use {@link ChatServer#shutdown()} instead.
     * 
     * @return 
     */
    static Message createShutdown() {
        return new Message(MessageType.SHUTDOWN, null, null);
    }
    
    /**
     * @return the messageType
     */
    public MessageType getMessageType() {
        return messageType;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the session
     */
    public Session getSession() {
        return session;
    }

    @Override
    public String toString() {
        return "Message{" + "messageType=" + messageType + ", message=" + message + ", session=" + session + '}';
    }    
    
}
