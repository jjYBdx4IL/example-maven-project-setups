package com.github.jjYBdx4IL.maven.examples.aspectj;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class ContentGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(ContentGenerator.class);
    
    @Tx
    public String getContent() {
        LOG.info("@Tx getContent()");
        return "Load-time weaving failed!";
    }
}
