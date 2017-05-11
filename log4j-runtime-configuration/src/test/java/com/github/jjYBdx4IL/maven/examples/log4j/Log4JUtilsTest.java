package com.github.jjYBdx4IL.maven.examples.log4j;

import com.github.jjYBdx4IL.test.FileUtil;
import com.github.jjYBdx4IL.utils.Log4JUtils;
import java.io.File;
import org.apache.commons.io.FileUtils;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class Log4JUtilsTest {

    private static final Logger LOG = LoggerFactory.getLogger(Log4JUtilsTest.class);
    private File tempDir = null;
    
    @Before
    public void before() {
        tempDir = FileUtil.createMavenTestDir(Log4JUtilsTest.class);
    }
    
    @Test
    public void test() throws Exception {
        // force any potential lazy logger initialization
        LOG.error("test1");
        
        File logFile = new File(tempDir, "log");
        
        assertFalse(logFile.exists());

        Log4JUtils.addFileAppender(logFile.getAbsolutePath());
        
        LOG.error("test2");
        
        assertTrue(logFile.exists());
        
        String logFileContents = FileUtils.readFileToString(logFile);
        assertNotNull(logFileContents);
        assertTrue(logFileContents.contains("test2"));
    }
}
