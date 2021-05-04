package com.github.jjYBdx4IL.maven.examples;

import com.github.jjYBdx4IL.utils.ProcRunner;
import static com.jcabi.matchers.RegexMatchers.matchesPattern;
import java.io.File;
import java.io.IOException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class MainIT {
    
    private static final Logger LOG = LoggerFactory.getLogger(MainIT.class);
    
    /**
     * Test of main method, of class Main.
     */
    @Test
    public void testMain() throws IOException {
        String classpathJarFile = System.getProperty("classpath.jar.file");
        assertNotNull(classpathJarFile);
        LOG.info("classpathJarFile: " + classpathJarFile);
        assertTrue(new File(classpathJarFile).exists());
        ProcRunner runner = new ProcRunner("java", "-jar", classpathJarFile, Main.class.getName());
        int exitCode = runner.run(60000L);
        assertEquals(runner.getOutputBlob(), 0, exitCode);
        assertThat(runner.getOutputBlob(), matchesPattern(".*started\\r?\\n$"));
    }

}