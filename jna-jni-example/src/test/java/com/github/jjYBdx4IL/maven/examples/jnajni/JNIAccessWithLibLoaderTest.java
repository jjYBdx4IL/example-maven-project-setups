package com.github.jjYBdx4IL.maven.examples.jnajni;

import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author jjYBdx4IL
 */
public class JNIAccessWithLibLoaderTest {

    // not working yet, open issue at https://github.com/scijava/native-lib-loader/issues/15
    @Ignore
    @Test
    public void testSum() {
        assertEquals(6, JNIAccessWithLibLoader.sum(new int[]{0, 1, 2, 3}));
    }

    @Ignore
    @Test
    public void testInc() {
        assertEquals(2, JNIAccessWithLibLoader.inc(1));
    }

}
