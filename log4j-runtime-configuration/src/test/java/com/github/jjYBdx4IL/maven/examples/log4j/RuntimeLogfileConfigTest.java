package com.github.jjYBdx4IL.maven.examples.log4j;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.FileAppender;
import org.apache.log4j.PatternLayout;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jjYBdx4IL.utils.env.Maven;

/**
 * Beware! This method adds the appender to the root looger. If there are other logger
 * elements specified in your log4j configuration (file), you need to set their
 * additivity attribute to true.
 * 
 * @author jjYBdx4IL
 */
public class RuntimeLogfileConfigTest {

    private static final Logger LOG = LoggerFactory.getLogger(RuntimeLogfileConfigTest.class);
    private File tempDir = null;
    
    @Before
    public void before() {
        tempDir = Maven.getTempTestDir(RuntimeLogfileConfigTest.class);
    }
    
    @Test
    public void test() throws Exception {
        // force any potential lazy logger initialization
        LOG.error("test1");
        
        File logFile = new File(tempDir, "log");
        
        assertFalse(logFile.exists());
        
        // add some file appender
        PatternLayout patternLayout = new PatternLayout("%d{ISO8601} %-5p [%-11t] [%-50c{4}] %L %m%n");
        FileAppender appender = new FileAppender(patternLayout, logFile.getAbsolutePath(), true);
        // configure the appender here, with file location, etc
        appender.activateOptions();

        org.apache.log4j.LogManager.getRootLogger().addAppender(appender);
        
        LOG.error("test2");
        
        assertTrue(logFile.exists());
        
        String logFileContents = FileUtils.readFileToString(logFile);
        assertNotNull(logFileContents);
        assertFalse(logFileContents.contains("test1"));
        assertTrue(logFileContents.contains("test2"));
    }
}
 