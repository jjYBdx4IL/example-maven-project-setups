package com.github.jjYBdx4IL.maven.examples.gwt.sandbox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class SandboxIT {

    private static final String SERVER_URL = "http://localhost:" + System.getProperty("jetty.port", "8080") + "/";

    public SandboxIT() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testWebAppLinks() throws Exception {
//        WebSiteVerifier verifier = new WebSiteVerifier();
//        final List<String> dontAccessPaths = new ArrayList<>();
//        dontAccessPaths.add("/WEB-INF/");
//        dontAccessPaths.add("/META-INF/");
//        dontAccessPaths.add("/smartgwtdesktop/sc/skins/");
//        verifier.setFailFast(true);
//        verifier.addDownloadURLCallback(new DownloadURLCallback() {
//            @Override
//            public boolean shouldDownloadURL(URL url) {
//                for (String s : dontAccessPaths) {
//                    if (url.getPath().equals(s)) {
//                        return false;
//                    }
//                }
//                return true;
//            }
//        });
//        List<LinkItem> failures = verifier.go(SERVER_URL);
//        for (LinkItem item : failures) {
//            logger.info("failure: " + item);
//        }
//        assertEquals(0, failures.size());
    }
}
