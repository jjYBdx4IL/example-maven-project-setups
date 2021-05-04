package com.github.jjYBdx4IL.maven.examples.annotations;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author jjYBdx4IL
 */
public class GeneratedByAnnotationProcessorTest {

    @Test
    public void test1() {
        GeneratedByAnnotationProcessor gbap = new GeneratedByAnnotationProcessor();
        assertTrue(gbap.toString().contains(ExampleImplementation1.class.getName()));
        assertTrue(gbap.toString().contains(ExampleImplementation2.class.getName()));
        assertFalse(gbap.toString().contains(ExampleImplementationMissingAnnotation.class.getName()));
    }

}
