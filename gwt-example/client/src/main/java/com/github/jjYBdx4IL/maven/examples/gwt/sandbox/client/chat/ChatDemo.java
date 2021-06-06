package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.chat;

import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.api.ChatMessage;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.api.DebugId;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.api.IChatMessagePseudoService;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.ResourceBundle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;

/**
 *
 * @author jjYBdx4IL
 */
public class ChatDemo extends FlowPanel implements ClickHandler,
        MessageListener<ChatMessage> {

    private final TextBox textBox;
    private final Button sendButton;
    private final TextArea chatLog;
    private final ClientMessageService clientMessageService
            = ClientMessageService.getInstance(IChatMessagePseudoService.class,
                    "/events/");

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
        
        textBox.ensureDebugId(DebugId.ChatDemoTextBox.name());
        sendButton.ensureDebugId(DebugId.ChatDemoSendButton.name());
        chatLog.ensureDebugId(DebugId.ChatDemoChatLog.name());
    }

    @Override
    protected void onLoad() {
        clientMessageService.addMessageListener(ChatMessage.class, this);
    }

    @Override
    public void onClick(ClickEvent event) {
        ChatMessage chatMessage = new ChatMessage("default", textBox.getText());
        clientMessageService.send(chatMessage);
    }

    private void append(String text) {
        chatLog.setText(chatLog.getText() + text + "\n");
    }

    @Override
    public void onMessage(ChatMessage chatMessage) {
        append(chatMessage.getText());
    }

}
