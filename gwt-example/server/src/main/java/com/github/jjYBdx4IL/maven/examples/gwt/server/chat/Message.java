package com.github.jjYBdx4IL.maven.examples.gwt.server.chat;

import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.api.ChatMessage;
import org.eclipse.jetty.websocket.api.Session;

/**
 *
 * @author jjYBdx4IL
 */
public class Message {

    private final MessageType messageType;
    private final ChatMessage chatMessage; // chat message object exchanged with clients
    private final Session session;

    public Message(MessageType messageType, ChatMessage message, Session session) {
        this.messageType = messageType;
        this.chatMessage = message;
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
    
    public static Message createMsg(ChatMessage message) {
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
    public ChatMessage getChatMessage() {
        return chatMessage;
    }

    /**
     * @return the session
     */
    public Session getSession() {
        return session;
    }

    @Override
    public String toString() {
        return "Message{" + "messageType=" + messageType + ", message=" + chatMessage + ", session=" + session + '}';
    }    
    
}
