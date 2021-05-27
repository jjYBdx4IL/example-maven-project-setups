package testroot;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import java.io.IOException;

public class LoggingTest {

    private static final Log LOG = LogFactory.getLog(LoggingTest.class);
    
    // it's possible to configure commons-logging programmatically. However, you have to make sure that
    // the factory hasn't been used yet. You can also call LogFactory.releaseAll(). That doesn't seem to affect
    // previously acquired logger instances, though it's likely not its intended purpose.
    static {
        assertNull(System.getProperty("org.apache.commons.logging.Log"));
        assertNull(System.getProperty("org.apache.commons.logging.simplelog.showdatetime"));
        LogFactory.releaseAll();
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.log.testroot", "DEBUG");
    }
    private static final Log LOG2 = LogFactory.getLog(LoggingTest.class);
    
    // once SimpleLog is initialized, updating sys props won't have any effect
    static {
        LogFactory.releaseAll();
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
    }
    private static final Log LOG3 = LogFactory.getLog(LoggingTest.class);

    @Test
    public void test1() throws Exception {
        
        LOG.trace("Hello, World!");
        
        LOG.debug("Hello, World!");
        assertFalse(LOG.isDebugEnabled());
        
        LOG.info("Hello, World!");
        assertTrue(LOG.isInfoEnabled());

        LOG.warn("Hello, World!");
        
        LOG.error("Hello, World!");
        
        LOG.fatal("Hello, World!");
        LOG.fatal("Hello, World!", new IOException("test exception"));
        
        LOG.fatal("Hello, World!");
    }
    
    @Test
    public void test2() throws Exception {
        assertFalse(LOG2.isTraceEnabled());
        assertTrue(LOG2.isDebugEnabled());
        
        LOG2.info("Hello, World! - now using SimpleLog without timestamps");
    }
    
    @Test
    public void test3() throws Exception {
        LOG3.info("Hello, World! - now using SimpleLog still without timestamps");
    }
}
