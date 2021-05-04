package com.github.jjYBdx4IL.maven.examples.aspectj;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jjYBdx4IL
 */
public class ContentGeneratorTest {

    @Test
    public void testGetContent() {
        assertEquals("Load-time weaving works with jetty!", new ContentGenerator().getContent());
    }

}