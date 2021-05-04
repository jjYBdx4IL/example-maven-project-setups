package com.github.jjYBdx4IL.maven.examples.server.chat;

import javax.servlet.ServletException;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
@SuppressWarnings("serial")
public class ChatServlet extends WebSocketServlet {

    private static final Logger LOG = LoggerFactory.getLogger(ChatServlet.class);
    private ChatServer chatServer = null;
    private Thread chatServerThread = null;

    public ChatServlet() {
        super();
        LOG.debug("ChatServlet constructor called {}", this);
    }

    @Override
    public void destroy() {
        super.destroy(); //To change body of generated methods, choose Tools | Templates.
        LOG.info("destroy called on {}", this);
        chatServer.shutdown();
    }

    @Override
    public void init() throws ServletException {
        super.init(); //To change body of generated methods, choose Tools | Templates.
        LOG.info("init called on {}", this);
        chatServerThread = new Thread(chatServer, "ChatServer");
        chatServerThread.start();
    }

    @Override
    public void configure(WebSocketServletFactory factory) {
        // set a 10 second idle timeout
        //factory.getPolicy().setIdleTimeout(10000);
        // register my socket
        //factory.register(ServerChatSocket.class);
        chatServer = new ChatServer();
        factory.setCreator(new ChatCreator(chatServer)); //
    }

}
