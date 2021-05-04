package com.github.jjYBdx4IL.maven.examples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);
    
    public static void main(String[] args) {
        new Main().run();
    }
    
    public void run() {
        LOG.info("started");
    }
}
