package com.github.jjYBdx4IL.maven.examples.aspectj;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Locale;
import javax.servlet.Servlet;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.After;

/**
 *
 * @author jjYBdx4IL
 */
public class ServletTestBase {

    protected Server server = null;

    protected void startServer(Class<? extends Servlet> clazz) {
        try {
            server = new Server(0);
            
            HandlerCollection handlers = new HandlerCollection();
            ContextHandlerCollection contexts = new ContextHandlerCollection();
            handlers.setHandlers(new Handler[]{contexts, new DefaultHandler()});
            server.setHandler(handlers);
            
            ServletContextHandler rootContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
            rootContext.setContextPath("/");
            rootContext.addServlet(clazz, "/");
            contexts.setHandlers(new Handler[]{rootContext});
            
            server.start();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @After
    public void tearDown() throws Exception {
        if (server != null) {
            server.stop();
            server = null;
        }
    }

    protected String getServletOutput() {
        try {
            return IOUtils.toString(getURL());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    protected URL getURL() {
        try {
            return getURL(server, "/");
        } catch (MalformedURLException | UnknownHostException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static URL getURL(Server server, String path) throws MalformedURLException, UnknownHostException {
        ServerConnector connector = (ServerConnector) server.getConnectors()[0];
        InetAddress addr = InetAddress.getLocalHost();
        return new URL(
                String.format(Locale.ROOT, "%s://%s:%d%s",
                        "http", addr.getHostAddress(), connector.getLocalPort(), path));
    }

}
