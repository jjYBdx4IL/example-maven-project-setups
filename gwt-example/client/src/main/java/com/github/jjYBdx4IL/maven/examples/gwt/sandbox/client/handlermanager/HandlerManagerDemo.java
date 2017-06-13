package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.handlermanager;

import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.api.DebugId;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.shared.AbstractEventDemo;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HandlerManagerDemo extends AbstractEventDemo {

    private static final Logger LOG = Logger.getLogger(HandlerManagerDemo.class.getName());
    
    private HandlerManager handlerManager;

    public HandlerManagerDemo() {}

    @Override
    protected void preInit() {
        handlerManager = new HandlerManager(this);
        ensureDebugId(DebugId.HandlerManagerDemoBody.name());
    }
    
    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        LOG.log(Level.FINE, "addValueChangeHandler()");
        return handlerManager.addHandler(ValueChangeEvent.getType(), handler);
    }

    @Override
    public void onValueChange(ValueChangeEvent<String> event) {
        LOG.log(Level.FINE, "onValueChange()");
        //ValueChangeEvent.fire(this, event.getValue());
        handlerManager.fireEvent(event);
    }
}
