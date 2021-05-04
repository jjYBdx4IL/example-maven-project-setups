package com.github.jjYBdx4IL.maven.examples.aspectj.fieldaccess;

import com.github.jjYBdx4IL.maven.examples.aspectj.ServletTestBase;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author jjYBdx4IL
 */
public class FieldAccessServletTest extends ServletTestBase {

    /**
     * Test of doGet method, of class FieldAccessServlet.
     */
    @Test
    public void testDoGet() {
        startServer(FieldAccessServlet.class);
        assertEquals("Load-time weaving works with jetty!", getServletOutput());
    }
}
