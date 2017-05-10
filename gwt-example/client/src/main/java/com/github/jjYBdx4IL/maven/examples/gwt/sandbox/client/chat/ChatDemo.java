package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.chat;

import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.ResourceBundle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.sksamuel.gwt.websockets.Websocket;
import com.sksamuel.gwt.websockets.WebsocketListener;

/**
 *
 * @author jjYBdx4IL
 */
public class ChatDemo extends FlowPanel implements ClickHandler, WebsocketListener {

    private final TextBox textBox;
    private final Button sendButton;
    private final TextArea chatLog;
    private Websocket socket = null;

    public ChatDemo() {
        textBox = new TextBox();
        sendButton = new Button(ResourceBundle.message.send());
        sendButton.addClickHandler(this);
        chatLog = new TextArea();
        
        add(textBox);
        add(sendButton);
        add(chatLog);
        
        chatLog.getElement().getStyle().setWidth(100, Unit.PCT);
        chatLog.getElement().getStyle().setHeight(50, Unit.PCT);
    }

    private String getWebsocketURL() {
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
        
        return proto + "://" + hostname + port + "/events/";
    }
    
    @Override
    protected void onLoad() {
        String websocketURL = getWebsocketURL();
        socket = new Websocket(websocketURL);
        socket.addListener(this);
        socket.open();
        chatLog.setText("connected to: " + websocketURL + "\n");
    }

    @Override
    public void onClick(ClickEvent event) {
        socket.send(textBox.getText());
    }

    @Override
    public void onClose() {
        
    }

    @Override
    public void onMessage(String msg) {
        chatLog.setText(chatLog.getText() + msg + "\n");
    }

    @Override
    public void onOpen() {
        
    }
}
