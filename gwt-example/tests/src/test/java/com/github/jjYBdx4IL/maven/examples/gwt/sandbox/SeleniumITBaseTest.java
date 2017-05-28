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

import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeleniumITBaseTest extends SeleniumITBase {

    private static final Logger LOG = LoggerFactory.getLogger(SeleniumITBaseTest.class);

    @Override
    public String getSandboxLocation() {
        String location = System.getProperty("sandbox.location");
        LOG.debug("sandbox location = " + location);
        assertNotNull(location);
        assertTrue(!location.isEmpty());
        return location;
    }

}
