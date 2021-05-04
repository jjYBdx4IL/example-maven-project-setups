package com.github.jjYBdx4IL.maven.examples.server.chat;

import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.api.ChatMessage;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.api.WebSocketPong;
import com.google.gwt.user.client.rpc.SerializationException;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketException;
import org.eclipse.jetty.websocket.api.WriteCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatServer implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(ChatServer.class);

    private final Collection<Session> sessions = new HashSet<>();
    private final LinkedBlockingQueue<Message> userqueue = new LinkedBlockingQueue<>();
    // high priority queue for internal messages, so operational stuff isn't delayed by high
    // user load
    private final LinkedBlockingQueue<Message> highprio = new LinkedBlockingQueue<>();
    private final CountDownLatch shutdownLatch = new CountDownLatch(1);

    private long nTextMessagesReceived = 0;
    private long nTextMessagesSent = 0;
    private long nPongsSent = 0;

    public ChatServer() {
    }

    public void add(Message message) {
        if (message == null) {
            throw new NullPointerException();
        }
        try {
            userqueue.add(message);
        } catch (IllegalStateException ex) {
            LOG.error("dropped message: " + message, ex);
        }
        LOG.info("messages in queue: " + userqueue.size());
    }

    private ChatMessage createReply(ChatMessage message) {
        String room = message.getRoom();
        if (room == null) {
            room = "lobby";
        }
        String text = "Received TEXT message: >>>" + message.toString() + "<<<";
        ChatMessage reply = new ChatMessage(room, text);
        return reply;
    }

    private void send(Object obj, Session sess) {
        String serializedReply;
        try {
            serializedReply = GWTSerializationUtils.serializeMessage(obj);
        } catch (SerializationException ex) {
            LOG.error("failed to serialize: " + obj, ex);
            return;
        }
        try {
            LOG.info("sending " + serializedReply + " to " + sess.getRemoteAddress());
            sess.getRemote().sendString(serializedReply, new WriteCallback() {
                @Override
                public void writeFailed(Throwable x) {
                    LOG.warn("write failed");
                }

                @Override
                public void writeSuccess() {
                    nPongsSent++;
                    LOG.info("nPongsSent: " + nPongsSent);
                }
            });
        } catch (WebSocketException ex) {
            LOG.error("", ex);
            add(Message.createDisonnect(sess));
        }
    }

    private void send(Message message) {
        ChatMessage reply = createReply(message.getChatMessage());
        String serializedReply;
        try {
            serializedReply = GWTSerializationUtils.serializeMessage(reply);
        } catch (SerializationException ex) {
            LOG.error("failed to serialize: " + reply, ex);
            return;
        }
        for (Session sess : sessions) {
            try {
                LOG.info("sending " + serializedReply + " to " + sess.getRemoteAddress());
                sess.getRemote().sendString(serializedReply, new WriteCallback() {
                    @Override
                    public void writeFailed(Throwable x) {
                        LOG.warn("write failed");
                    }

                    @Override
                    public void writeSuccess() {
                        nTextMessagesSent++;
                        LOG.info("nTextMessagesSent: " + getnTextMessagesSent());
                    }
                });
            } catch (WebSocketException ex) {
                LOG.error("", ex);
                add(Message.createDisonnect(sess));
            }
        }
    }

    @Override
    public void run() {
        LOG.info("chat server thread started");
        boolean shutdown = false;
        while (!shutdown) {
            LOG.info("server loop start");
            Message message = highprio.poll();
            if (message == null) {
                try {
                    message = userqueue.take();
                } catch (InterruptedException ex) {
                    LOG.error("", ex);
                    continue;
                }
            }
            switch (message.getMessageType()) {
                case SHUTDOWN:
                    shutdown = true;
                    continue;
                case MSG:
                    nTextMessagesReceived++;
                    LOG.info("nTextMessages: " + getnTextMessagesReceived());
                    send(message);
                    break;
                case CONNECT:
                    sessions.add(message.getSession());
                    LOG.info("connections total: " + sessions.size());
                    break;
                case DISCONNECT:
                    sessions.remove(message.getSession());
                    LOG.info("connections total: " + sessions.size());
                    break;
                case PING:
                    LOG.info("ping received");
                    send(new WebSocketPong(), message.getSession());
                    break;
            }
        }
        LOG.info("chat server thread stopped");
        shutdownLatch.countDown();
    }

    public void shutdown() {
        LOG.info("signaling shutdown to chat server thread");
        try {
            highprio.add(Message.createShutdown());
        } catch (IllegalStateException ex) {
            LOG.error("failed to add shutdown message to high prio queue", ex);
        }

        // make sure the server gets the shutdown message if it is listening to
        // the user queue:
        try {
            userqueue.add(Message.createShutdown());
        } catch (IllegalStateException ex) {
            LOG.error("failed to add shutdown message to regular queue", ex);
        }
    }

    public void wait4Shutdown(long timeoutSecs) {
        LOG.info("wait4Shutdown started");
        try {
            if (!shutdownLatch.await(timeoutSecs, TimeUnit.SECONDS)) {
                throw new RuntimeException();
            }
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        LOG.info("wait4Shutdown done");
    }

    /**
     * Only for testing. Shut down the server first, there is no explicit synchronization.
     *
     * @return the nTextMessagesReceived
     */
    protected long getnTextMessagesReceived() {
        return nTextMessagesReceived;
    }

    /**
     * Only for testing. Shut down the server first, there is no explicit synchronization.
     *
     * @return the nTextMessagesSent
     */
    protected long getnTextMessagesSent() {
        return nTextMessagesSent;
    }

    /**
     * Only for testing. Shut down the server first, there is no explicit synchronization.
     */
    protected Collection<Session> getSessions() {
        return sessions;
    }

}
