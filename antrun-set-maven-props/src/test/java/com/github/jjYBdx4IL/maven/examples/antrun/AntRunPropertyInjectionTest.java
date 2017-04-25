package com.github.jjYBdx4IL.maven.examples.antrun;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author jjYBdx4IL
 */
public class AntRunPropertyInjectionTest {

    @Test
    public void test() throws IOException {
        assertEquals("<replaced> NOT replACED" + System.lineSeparator(),
                IOUtils.toString(getClass().getResource("testfile.txt"), "ASCII"));
    }
}
