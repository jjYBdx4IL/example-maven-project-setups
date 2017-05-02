package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.eventbus;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.shared.AbstractEventDemo;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleEventBusDemo extends AbstractEventDemo {

    private static final Logger LOG = Logger.getLogger(SimpleEventBusDemo.class.getName());
    
    private EventBus eventBus;

    public SimpleEventBusDemo() {}

    protected void preInit() {
        eventBus = new SimpleEventBus();
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        LOG.log(Level.FINE, "addValueChangeHandler()");
        return eventBus.addHandler(ValueChangeEvent.getType(), handler);
    }

    @Override
    public void onValueChange(ValueChangeEvent<String> event) {
        LOG.log(Level.FINE, "onValueChange()");
        //ValueChangeEvent.fire(this, event.getValue());
        eventBus.fireEvent(event);
    }
}
