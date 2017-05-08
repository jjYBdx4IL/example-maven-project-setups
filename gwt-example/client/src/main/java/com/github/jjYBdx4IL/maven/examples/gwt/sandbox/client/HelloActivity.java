package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client;


import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.logging.Logger;

public class HelloActivity extends AbstractActivity implements HelloView.Presenter {
    private static final Logger logger = Logger.getLogger(HelloActivity.class.getName());
    // Used to obtain views, eventBus, placeController
    // Alternatively, could be injected via GIN
    private ClientFactory clientFactory;
    // Name that will be appended to "Hello,"
    private String name;

    public HelloActivity(HelloPlace place, ClientFactory clientFactory) {
        logger.info("new()");
        this.name = place.getHelloName();
        this.clientFactory = clientFactory;
    }

    /**
     * Invoked by the ActivityManager to start a new Activity
     */
    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        logger.info("start()");
        logger.info("containerWidget = "+(containerWidget instanceof SimplePanel));
        HelloView helloView = clientFactory.getHelloView();
        helloView.setName(name);
        helloView.setPresenter(this);
        Widget w = helloView.asWidget();
        w.addStyleName("testProbeHelloViewWidget");
        containerWidget.setWidget(w);
    }

    /**
     * Ask user before stopping this activity
     */
    @Override
    public String mayStop() {
        logger.info("mayStop()");
        return null;
    }

    /**
     * Navigate to a new Place in the browser
     */
    @Override
    public void goTo(Place place) {
        logger.info("goTo "+place);
        clientFactory.getPlaceController().goTo(place);
    }
}
