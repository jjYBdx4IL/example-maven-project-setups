package com.github.jjYBdx4IL.maven.examples.gwt.server.chat;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class ChatServlet extends WebSocketServlet {

    private static final Logger LOG = Logger.getLogger(ChatServlet.class.getName());
    private ChatServer chatServer = null;
    private Thread chatServerThread = null;

    public ChatServlet() {
        super();
        LOG.log(Level.INFO, "TestServlet constructor called {0}", this);
    }

    @Override
    public void destroy() {
        super.destroy(); //To change body of generated methods, choose Tools | Templates.
        LOG.log(Level.INFO, "destroy called on {0}", this);
        chatServer.shutdown();
    }

    @Override
    public void init() throws ServletException {
        super.init(); //To change body of generated methods, choose Tools | Templates.
        LOG.log(Level.INFO, "init called on {0}", this);
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
