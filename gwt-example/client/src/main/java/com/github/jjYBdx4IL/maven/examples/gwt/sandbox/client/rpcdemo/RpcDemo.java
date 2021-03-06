package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.rpcdemo;

import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.api.DebugId;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.ResourceBundle;
import static com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.ResourceBundle.message;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.events.LoginEvent;
import static com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.rpcdemo.ResourceBundle.RES;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueListBox;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RpcDemo extends FlowPanel {

    private TextBox textBox;
    private Label rpcReplyLabel;
    private Button loginButton;
    private Button logoutButton;
    private Button greetMeButton;
    private Button forceOomButton;
    private Button startAnimButton;
    private Button stopAnimButton;
    private Animation animation;
    private TextBox fpsTextBox;
    private ListBox listBox;
    private ValueListBox enumListBox;
    
    private final EventBus eventBus;

    @Inject
    public RpcDemo(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    protected void onLoad() {
        RES.getStyle().ensureInjected();

        textBox = new TextBox();
        textBox.ensureDebugId(DebugId.RpcDemoTextBox.name());
        loginButton = new Button(ResourceBundle.message.loginDesc());
        logoutButton = new Button(ResourceBundle.message.logoutDesc());
        greetMeButton = new Button(ResourceBundle.message.greetMeDesc());
        greetMeButton.ensureDebugId(DebugId.RpcDemoGreetMeButton.name());
        rpcReplyLabel = new Label(ResourceBundle.message.wait4input());
        rpcReplyLabel.ensureDebugId(DebugId.RpcDemoReplyLabel.name());
        forceOomButton = new Button(ResourceBundle.message.forceOOM());
        startAnimButton = new Button(message.start());
        stopAnimButton = new Button(message.stop());
        fpsTextBox = new TextBox();
        listBox = new ListBox();
        listBox.ensureDebugId(DebugId.RpcDemoListBox.name());
        enumListBox = new ValueListBox<EnumForValueListBox>(new Renderer<EnumForValueListBox>() {

            @Override
            public String render(EnumForValueListBox object) {
                if (object == null) {
                    return "null";
                }
                switch (object) {
                    case ONE:
                        return "one - 1";
                    case TWO:
                        return "two - 2";
                    case THREE:
                        return "three - 3";
                    default:
                        return "default";
                }
            }

            @Override
            public void render(EnumForValueListBox object, Appendable appendable) throws IOException {
                appendable.append(render(object));
            }
        });

        listBox.addItem("first listbox entry");
        listBox.addItem("second listbox entry");
        listBox.addItem("third listbox entry");

        List<EnumForValueListBox> list = new ArrayList<>();
        list.add(null);
        list.addAll(Arrays.asList(EnumForValueListBox.values()));
        enumListBox.setAcceptableValues(list);
        enumListBox.setValue(EnumForValueListBox.TWO);
        if (enumListBox.getValue() != null && !(enumListBox.getValue() instanceof EnumForValueListBox)) {
            throw new RuntimeException();
        }

        loginButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ResourceBundle.asyncService.login("user123", "secret", new LoginCallback());
            }
        });

        logoutButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ResourceBundle.asyncService.logout(new LogoutCallback());
            }
        });
        
        greetMeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                rpcReplyLabel.setText(ResourceBundle.message.wait4reply());
                ResourceBundle.asyncService.greetme(textBox.getText(), new ButtonCallback());
            }
        });

        forceOomButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ResourceBundle.asyncService.forceOOM(null);
            }
        });

        animation = new Animation(1000, 400);

        add(loginButton);
        add(logoutButton);
        add(textBox);
        add(greetMeButton);
        add(rpcReplyLabel);
        add(forceOomButton);
        add(animation);
        add(startAnimButton);
        add(stopAnimButton);
        add(fpsTextBox);
        add(listBox);
        add(enumListBox);

        startAnimButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                animation.start();
            }
        });
        stopAnimButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                fpsTextBox.setValue(Float.toString(animation.stop()) + " fps");
            }
        });
    }

    private class ButtonCallback implements AsyncCallback<String> {

        @Override
        public void onFailure(Throwable caught) {
            Window.alert(caught.toString());
            rpcReplyLabel.setText(ResourceBundle.message.wait4input());
        }

        @Override
        public void onSuccess(String result) {
            rpcReplyLabel.setText(ResourceBundle.message.receivedReply(result));
        }
    }

    private class LoginCallback implements AsyncCallback<Void> {

        @Override
        public void onFailure(Throwable caught) {
            Window.alert(caught.toString());
        }

        @Override
        public void onSuccess(Void result) {
            // trigger XSRF token fetch after login
            eventBus.fireEvent(new LoginEvent());
        }
    }

    private class LogoutCallback implements AsyncCallback<Void> {

        @Override
        public void onFailure(Throwable caught) {
            Window.alert(caught.toString());
        }

        @Override
        public void onSuccess(Void result) {
        }
    }
}
