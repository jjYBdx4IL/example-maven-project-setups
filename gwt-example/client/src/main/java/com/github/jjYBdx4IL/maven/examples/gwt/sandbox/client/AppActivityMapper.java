package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client;


import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import java.util.logging.Logger;

public class AppActivityMapper implements ActivityMapper {
    private static final Logger logger = Logger.getLogger(AppActivityMapper.class.getName());
    private ClientFactory clientFactory;

    public AppActivityMapper(ClientFactory clientFactory) {
        super();
        logger.info("new()");
        this.clientFactory = clientFactory;
    }

    @Override
    public Activity getActivity(Place place) {
        logger.info("getActivity "+place);
        if (place instanceof HelloPlace)
            return new HelloActivity((HelloPlace) place, clientFactory);
        else if (place instanceof GoodbyePlace)
            return new GoodbyeActivity((GoodbyePlace) place, clientFactory);
        return null;
    }
}
