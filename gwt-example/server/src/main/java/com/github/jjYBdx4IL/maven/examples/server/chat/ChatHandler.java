package com.github.jjYBdx4IL.maven.examples.server.chat;

import org.eclipse.jetty.util.component.Container;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class ChatHandler extends WebSocketHandler implements LifeCycle.Listener, Container.Listener {

    /**
     * @return the chatServer
     */
    public ChatServer getChatServer() {
        return chatServer;
    }

    private static final Logger LOG = LoggerFactory.getLogger(ChatHandler.class);
    
    private final ChatServer chatServer = new ChatServer();
    
    public ChatHandler() {
    }
    
    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.setCreator(new ChatCreator(getChatServer()));
        addLifeCycleListener(this);
        addEventListener(this);
    }

    @Override
    public void lifeCycleStarting(LifeCycle event) {
        LOG.info("lifeCycleStarting");
    }

    @Override
    public void lifeCycleStarted(LifeCycle event) {
        LOG.info("lifeCycleStarted");
        Thread thread = new Thread(getChatServer(), "ChatServerThread");
        thread.start();
    }

    @Override
    public void lifeCycleFailure(LifeCycle event, Throwable cause) {
        LOG.info("lifeCycleFailure");
    }

    @Override
    public void lifeCycleStopping(LifeCycle event) {
        LOG.info("lifeCycleStopping");
        getChatServer().shutdown();
    }

    @Override
    public void lifeCycleStopped(LifeCycle event) {
        LOG.info("lifeCycleStopped");
    }

    @Override
    public void beanAdded(Container parent, Object child) {
        LOG.info("beanAdded");
    }

    @Override
    public void beanRemoved(Container parent, Object child) {
        LOG.info("beanRemoved");
    }

}
