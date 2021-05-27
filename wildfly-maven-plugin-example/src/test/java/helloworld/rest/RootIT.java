/*
 * Copyright © 2019 jjYBdx4IL (https://github.com/jjYBdx4IL)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package helloworld.rest;

import static org.junit.Assert.assertEquals;

import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RootIT {

    private static final Logger LOG = LoggerFactory.getLogger(RootIT.class);

    private static String pageUrl = "http://localhost:8082";

    @Test
    public void test() throws Exception {
        String response = IOUtils.toString(new URL(pageUrl), StandardCharsets.UTF_8);
        LOG.info("response = " + response);
        assertEquals("Hello, World!", response);
    }

}
