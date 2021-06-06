package com.github.jjYBdx4IL.maven.examples.server.chat;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Future;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.junit.After;
import static org.junit.Assert.assertEquals;

import com.github.jjYBdx4IL.maven.examples.server.chat.ChatHandler;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public abstract class WebSocketChatTestBase {

    private static final Logger LOG = LoggerFactory.getLogger(WebSocketChatTestBase.class);

    protected Server server = null;
    private final LinkedList<WebSocketClient> clients = new LinkedList<>();
    private final Map<WebSocketClient, ClientEventSocket> eventSockets = new HashMap<>();
    private final Map<WebSocketClient, Session> sessions = new HashMap<>();
    public static final int NUM_CLIENTS = 10;
    protected final ChatHandler chatHandler = new ChatHandler();
    protected final static String CTX_PATH = "/events/";

    @After
    public void after() throws Exception {
        if (server != null) {
            server.stop();
        }
        for (WebSocketClient client : eventSockets.keySet()) {
            client.stop();
        }
    }

    protected abstract void configureServer();

    // cannot run this test because GWT RPC serialization is different when sent from client
    // TODO: maybe use htmlunit GwtTestCase?
    @Ignore
    @Test
    public void testWebSocket() throws Exception {
        server = new Server(0);
        configureServer();

        server.start();
        server.dump(System.err);

        URI uri = getURI(server);
        LOG.info(uri.toString());

        int nExpextedReceivedByServer = 0;
        int nExpectedSentByServer = 0;

        int nExpectedReceivedByClient = 0;
        for (int i = 0; i < NUM_CLIENTS; i++) {
            nExpectedReceivedByClient += NUM_CLIENTS - i;
            WebSocketClient client = new WebSocketClient();
            client.start();
            // The socket that receives events
            ClientEventSocket socket = new ClientEventSocket(nExpectedReceivedByClient);
            // Attempt Connect
            Future<Session> fut = client.connect(socket, uri);
            // Wait for Connect
            Session session = fut.get();

            clients.add(client);
            eventSockets.put(client, socket);
            sessions.put(client, session);
        }

        LOG.info("all clients connected");

        // Start with NUM_CLIENTS connected to the server.
        // During each iteration each connected client send one message.
        // After each iteration we terminate one client until there are no clients left.
        do {
            // send one message from each client remaining
            for (WebSocketClient client : clients) {
                Session sess = sessions.get(client);
                sess.getRemote().sendString("Hello", null);
                nExpextedReceivedByServer++;
                nExpectedSentByServer += clients.size();
            }
            
            // terminate one client
            WebSocketClient client = clients.remove();
            ClientEventSocket clientEventSocket = eventSockets.remove(client);
            // wait for the number of expected messages received from server
            clientEventSocket.await(10L);
            Session session = sessions.remove(client);
            session.close();
        } while (!clients.isEmpty());

        LOG.info("all replies received");

        //Thread.sleep(3000L);
        
        server.stop();

        chatHandler.getChatServer().wait4Shutdown(10L);

        // check chat server state
        long nActiveSessionsOnServer = chatHandler.getChatServer().getSessions().size();
        long nReceivedByServer = chatHandler.getChatServer().getnTextMessagesReceived();
        long nSentByServer = chatHandler.getChatServer().getnTextMessagesSent();
        LOG.info("nActiveSessionsOnServer: " + nActiveSessionsOnServer);
        LOG.info("nReceivedByServer: " + nReceivedByServer);
        LOG.info("nSentByServer: " + nSentByServer);
        assertEquals(0, nActiveSessionsOnServer);
        assertEquals(nExpextedReceivedByServer, nReceivedByServer);
        assertEquals(nExpectedSentByServer, nSentByServer);
    }

    public URI getURI(Server server) throws MalformedURLException, UnknownHostException {
        ServerConnector connector = (ServerConnector) server.getConnectors()[0];
        InetAddress addr = InetAddress.getLocalHost();
        return URI.create(String.format(Locale.ROOT, "%s://%s:%d%s", "ws", addr.getHostAddress(),
                connector.getLocalPort(), CTX_PATH));
    }

}
