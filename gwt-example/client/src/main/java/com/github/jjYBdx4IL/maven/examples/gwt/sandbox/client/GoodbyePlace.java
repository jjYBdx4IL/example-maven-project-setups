package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client;


import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import java.util.logging.Logger;

public class GoodbyePlace extends Place {
    private static final Logger logger = Logger.getLogger(GoodbyePlace.class.getName());
    private String helloName;

    public GoodbyePlace(String token) {
        logger.info("new()");
        this.helloName = token;
    }

    public String getHelloName() {
        logger.info("getHelloName()");
        return helloName;
    }

    public static class Tokenizer implements PlaceTokenizer<GoodbyePlace> {
        @Override
        public String getToken(GoodbyePlace place) {
            logger.info("getToken()");
            return place.getHelloName();
        }

        @Override
        public GoodbyePlace getPlace(String token) {
            logger.info("getPlace("+token+")");
            return new GoodbyePlace(token);
        }
    }
}
