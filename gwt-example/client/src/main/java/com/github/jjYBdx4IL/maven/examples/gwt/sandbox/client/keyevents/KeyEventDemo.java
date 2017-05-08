package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.keyevents;


import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import java.util.logging.Logger;

public class KeyEventDemo extends FlowPanel implements ValueChangeHandler<String>,
        KeyUpHandler, KeyDownHandler, KeyPressHandler {

    private static final Logger LOG = Logger.getLogger(KeyEventDemo.class.getName());
    Label title;
    TextBox textBox;
    TextBox textBoxKeyUp;
    TextBox textBoxKeyDown;
    TextBox textBoxKeyPress;
    EventBus eventBus;

    public KeyEventDemo() {
    }

    private void init() {
        title = new Label(getClass().getName());
        textBox = new TextBox();
        textBoxKeyUp = new TextBox();
        textBoxKeyDown = new TextBox();
        textBoxKeyPress = new TextBox();
        eventBus = new SimpleEventBus();

        this.add(title);
        this.add(textBox);
        this.add(textBoxKeyDown);
        this.add(textBoxKeyPress);
        this.add(textBoxKeyUp);

        textBox.addValueChangeHandler(this);
        textBox.addKeyDownHandler(this);
        textBox.addKeyPressHandler(this);
        textBox.addKeyUpHandler(this);
    }

    @Override
    protected void onAttach() {
        LOG.fine("entering onAttach()");
        super.onAttach();
        LOG.fine("leaving onAttach()");
    }

    @Override
    protected void onLoad() {
        LOG.fine("entering onLoad()");
        init();
        super.onLoad();
        LOG.fine("leaving onLoad()");
    }

    @Override
    public void onValueChange(ValueChangeEvent<String> event) {
        LOG.fine("entering onValueChange()");
        eventBus.fireEvent(event);
        LOG.fine("leaving onValueChange()");
    }

    @Override
    public void onKeyUp(KeyUpEvent event) {
        LOG.fine("entering onKeyUp()");
        textBoxKeyUp.setValue(event.toDebugString());
        LOG.fine("leaving onKeyUp()");
    }

    @Override
    public void onKeyDown(KeyDownEvent event) {
        LOG.fine("entering onKeyDown()");
        textBoxKeyDown.setValue(event.toDebugString());
        LOG.fine("leaving onKeyDown()");
    }

    @Override
    public void onKeyPress(KeyPressEvent event) {
        LOG.fine("entering onKeyPress()");
        textBoxKeyPress.setValue(event.toDebugString());
        LOG.fine("leaving onKeyPress()");
    }
}
