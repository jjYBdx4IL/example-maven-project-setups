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
import java.util.logging.Logger;
import org.jboss.errai.bus.client.ErraiBus;
import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.bus.client.api.messaging.MessageBus;
import org.jboss.errai.bus.client.api.messaging.MessageCallback;
import org.jboss.errai.bus.client.api.messaging.RequestDispatcher;
import org.jboss.errai.common.client.api.ErrorCallback;

/**
 *
 * @author jjYBdx4IL
 */
public class ChatDemo extends FlowPanel implements ClickHandler, MessageCallback {

    private static final Logger LOG = Logger.getLogger(ChatDemo.class.getName());
    
    private final TextBox textBox;
    private final Button sendButton;
    private final TextArea chatLog;
    private MessageBus bus = ErraiBus.get();
    private RequestDispatcher dispatcher = ErraiBus.getDispatcher();

    public ChatDemo() {
        textBox = new TextBox();
        textBox.setText("some text");
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
        bus.subscribe("BroadcastReceiver", this);
        chatLog.setText("connected to: BroadcastReceiver\n");
    }

    @Override
    public void callback(Message message) {
        PojoMessage pojoMessage = message.get(PojoMessage.class, "message");
        chatLog.setText(chatLog.getText() + pojoMessage.getText() + "\n");
    }

    @Override
    public void onClick(ClickEvent event) {
        PojoMessage pojoMessage = new PojoMessage();
        pojoMessage.setText(textBox.getText());

        MessageBuilder.createMessage()
                .toSubject("ErraiChatService")
                .signalling()
                .with("message", pojoMessage)
                .errorsHandledBy(new ErrorCallback() {
                    @Override
                    public boolean error(Object message, Throwable throwable) {
                        throwable.printStackTrace();
                        return true;
                    }
                })
                .sendNowWith(dispatcher);
    }

}
