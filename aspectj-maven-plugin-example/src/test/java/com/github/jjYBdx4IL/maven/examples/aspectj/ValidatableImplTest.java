package com.github.jjYBdx4IL.maven.examples.aspectj;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jjYBdx4IL
 */
public class ValidatableImplTest {

    @Test
    public void test() {
        assertEquals(0, new ValidatableImpl().testCounter);
        assertEquals(1, new ValidatableImpl("").testCounter);
    }

}