package com.github.jjYBdx4IL.maven.examples.jnajni;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author jjYBdx4IL
 */
public class JNIAccessTest {

    @Test
    public void testSum() {
        assertEquals(6, JNIAccess.sum(new int[]{0, 1, 2, 3}));
    }

    @Test
    public void testInc() {
        assertEquals(2, JNIAccess.inc(1));
    }

}
