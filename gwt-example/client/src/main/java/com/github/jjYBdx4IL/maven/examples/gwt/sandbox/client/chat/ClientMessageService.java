package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.chat;

import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.api.IChatMessagePseudoService;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.api.WebSocketPing;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamFactory;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.sksamuel.gwt.websockets.Websocket;
import com.sksamuel.gwt.websockets.WebsocketListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jjYBdx4IL
 */
public class ClientMessageService implements WebsocketListener {

    private static final Map<Class<? extends RemoteService>, ClientMessageService> INSTANCES
            = new HashMap<>();

    private static final int PING_IVAL_SECS = 55;
    private final Timer pingTimer;

    public static ClientMessageService getInstance(Class<? extends RemoteService> clazz,
            String remoteServiceRelativePath) {
        if (INSTANCES.containsKey(clazz)) {
            return INSTANCES.get(clazz);
        }
        ClientMessageService cms = new ClientMessageService(clazz, remoteServiceRelativePath);
        cms.connect();
        INSTANCES.put(clazz, cms);
        return cms;
    }

    private final SerializationStreamFactory factory;
    private final String remoteServiceRelativePath;
    private Websocket socket = null;

    private final Map<Class<?>, List<MessageListener<?>>> listeners = new HashMap<>();

    private ClientMessageService(Class clazz,
            String remoteServiceRelativePath) {
        if (IChatMessagePseudoService.class.equals(clazz)) {
            factory = (SerializationStreamFactory) GWT.create(IChatMessagePseudoService.class);
        } else {
            throw new IllegalArgumentException("RemoteService class " + clazz.getName() + " not known");
        }
        this.remoteServiceRelativePath = remoteServiceRelativePath;
        pingTimer = new Timer() {
            @Override
            public void run() {
                pingTimer.schedule(PING_IVAL_SECS * 1000);
                send(new WebSocketPing());
            }
        };
    }

    private void connect() {
        if (socket != null) {
            throw new IllegalStateException("connect already called");
        }
        String websocketURL = getWebsocketURL(remoteServiceRelativePath);
        socket = new Websocket(websocketURL);
        socket.addListener(this);
        socket.open();
        pingTimer.schedule(PING_IVAL_SECS * 1000);
    }

    public void addMessageListener(Class<?> aClass, MessageListener<?> aThis) {
        List<MessageListener<?>> listenersForClass = listeners.get(aClass);
        if (listenersForClass == null) {
            listenersForClass = new ArrayList<>();
            listeners.put(aClass, listenersForClass);
        }
        listenersForClass.add(aThis);
    }

    @Override
    public void onClose() {
        pingTimer.cancel();
    }

    @Override
    public void onMessage(String msg) {
        Object messageObject = deserialize(msg);
        Class<?> messageClass = messageObject.getClass();
        List<MessageListener<?>> listenersForClass = listeners.get(messageClass);
        if (listenersForClass == null) {
            return;
        }
        for (MessageListener listener : listenersForClass) {
            listener.onMessage(messageObject);
        }
    }

    @Override
    public void onOpen() {
    }

    private String serialize(Object message) {
        try {
            SerializationStreamWriter writer = factory.createStreamWriter();
            writer.writeObject(message);
            final String data = writer.toString();
            return data;
        } catch (final SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    private Object deserialize(String data) {
        try {
            final SerializationStreamReader streamReader = factory.createStreamReader(data);
            return streamReader.readObject();
        } catch (final SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    private String getWebsocketURL(String remoteServiceRelativePath) {
        String path = Window.Location.getPath();
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (!path.endsWith("/")) {
            path = path.substring(0, path.lastIndexOf("/") + 1);
        }
        if (remoteServiceRelativePath.startsWith("/")) {
            path += remoteServiceRelativePath.substring(1);
        } else {
            path += remoteServiceRelativePath;
        }
        String proto = "ws";
        if ("https:".equalsIgnoreCase(Window.Location.getProtocol())) {
            proto = "wss";
        }
        String hostname = Window.Location.getHostName();
        String port = Window.Location.getPort();
        if (port == null || port.isEmpty()) {
            port = "";
        } else {
            int iPort = Integer.parseInt(port);
            if ((iPort == 80 && proto.equals("ws")) || (iPort == 443 && proto.equals("wss"))) {
                port = "";
            }
        }
        if (!port.isEmpty()) {
            port = ":" + port;
        }

        // for debugging and testing:
        String debugWsPort = Window.Location.getParameter("wsPort");
        if (debugWsPort != null) {
            port = ":" + Integer.parseInt(debugWsPort);
        }

        return proto + "://" + hostname + port + path;
    }

    public void send(Object obj) {
        socket.send(serialize(obj));
    }
}
