package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client;


import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;

public interface ClientFactory {
    EventBus getEventBus();
    PlaceController getPlaceController();
    HelloView getHelloView();
    GoodbyeView getGoodbyeView();
}
