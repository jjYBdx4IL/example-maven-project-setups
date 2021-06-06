package com.github.jjYBdx4IL.maven.examples.server.chat;

import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatCreator implements WebSocketCreator
{
    private static final Logger LOG = LoggerFactory.getLogger(ChatCreator.class);
    
    private final ChatServer chatServer;

    public ChatCreator(ChatServer chatServer)
    {
        if (chatServer == null) {
            throw new NullPointerException();
        }
        this.chatServer = chatServer;
    }

    @Override
    public Object createWebSocket(ServletUpgradeRequest request, 
            ServletUpgradeResponse response)
    {
        LOG.info("createWebSocket");
        // We want to create the Chat Socket and associate
        // it with our chatroom implementation
        return new ServerChatSocket(chatServer);
    }

}