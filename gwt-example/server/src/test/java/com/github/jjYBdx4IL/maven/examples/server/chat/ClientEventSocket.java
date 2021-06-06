package com.github.jjYBdx4IL.maven.examples.server.chat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class ClientEventSocket extends WebSocketAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(ClientEventSocket.class);
    private final CountDownLatch messageReceivedCountDownLatch;
    
    public ClientEventSocket(int expectedMessages) {
        super();
        messageReceivedCountDownLatch = new CountDownLatch(expectedMessages);
    }
    
    @Override
    public void onWebSocketConnect(Session sess) {
        super.onWebSocketConnect(sess);
        LOG.info("Socket Connected: " + sess);
    }

    @Override
    public void onWebSocketText(String message) {
        super.onWebSocketText(message);
        LOG.info("Received TEXT message: >>>" + message + "<<<");
        messageReceivedCountDownLatch.countDown();
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);
        LOG.info("Socket Closed: [" + statusCode + "] " + reason);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        super.onWebSocketError(cause);
        LOG.error("", cause);
    }

    public void await(long timeoutSecs) {
        try {
            if (!messageReceivedCountDownLatch.await(timeoutSecs, TimeUnit.SECONDS)) {
                throw new RuntimeException();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    
}
