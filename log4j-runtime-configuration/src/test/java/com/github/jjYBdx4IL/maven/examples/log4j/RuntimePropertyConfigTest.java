package com.github.jjYBdx4IL.maven.examples.log4j;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.PropertyConfigurator;
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
public class RuntimePropertyConfigTest {

    private static final Logger LOG = LoggerFactory.getLogger(RuntimePropertyConfigTest.class);
    private File tempDir = null;
    
    @Before
    public void before() {
        tempDir = Maven.getTempTestDir(RuntimePropertyConfigTest.class);
    }
    
    @Test
    public void test() throws Exception {
        // force any potential lazy logger initialization
        LOG.error("test1");
        
        File logFile = new File(tempDir, "log");
        
        assertFalse(logFile.exists());
        
        Properties props = new Properties();
        props.setProperty("log4j.rootLogger", "INFO, file");
        props.setProperty("log4j.appender.file", org.apache.log4j.FileAppender.class.getName());
        props.setProperty("log4j.appender.file.File", logFile.getAbsolutePath());
        props.setProperty("log4j.appender.file.layout", org.apache.log4j.PatternLayout.class.getName());
        props.setProperty("log4j.appender.file.layout.ConversionPattern",
                "%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n");
        PropertyConfigurator.configure(props);
        
        LOG.error("test2");
        
        assertTrue(logFile.exists());
        
        String logFileContents = FileUtils.readFileToString(logFile);
        assertNotNull(logFileContents);
        assertFalse(logFileContents.contains("test1"));
        assertTrue(logFileContents.contains("test2"));
    }
}
