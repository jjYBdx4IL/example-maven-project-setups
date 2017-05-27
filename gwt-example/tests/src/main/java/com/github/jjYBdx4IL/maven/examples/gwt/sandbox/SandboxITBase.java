/*
 * Copyright (C) 2016 jjYBdx4IL (https://github.com/jjYBdx4IL)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

public class SandboxITBase {

    private static final String SERVER_URL = "http://localhost:" + System.getProperty("jetty.port", "8080") + "/";

    public SandboxITBase() {
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
