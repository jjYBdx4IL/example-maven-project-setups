package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client;


import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import java.util.logging.Logger;

public class HelloPlace extends Place {
    private static final Logger logger = Logger.getLogger(HelloPlace.class.getName());
    private String helloName;

    public HelloPlace(String token) {
        logger.info("new()");
        this.helloName = token;
    }

    public String getHelloName() {
        logger.info("getHelloName()");
        return helloName;
    }

    public static class Tokenizer implements PlaceTokenizer<HelloPlace> {
        @Override
        public String getToken(HelloPlace place) {
            logger.info("getToken()");
            return place.getHelloName();
        }

        @Override
        public HelloPlace getPlace(String token) {
            logger.info("getPlace("+token+")");
            return new HelloPlace(token);
        }
    }
}
