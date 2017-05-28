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

import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.api.DebugId;
import com.github.jjYBdx4IL.test.selenium.SeleniumTestBase;
import com.github.jjYBdx4IL.test.selenium.WebElementNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SeleniumITBase extends SeleniumTestBase {

    private static final Logger LOG = LoggerFactory.getLogger(SeleniumITBase.class);

    /**
     *
     * @return some URL string pointing to the location of the app to be tested
     */
    public abstract String getSandboxLocation();

    @Before
    public void before() {
        getDriver(SeleniumTestBase.Driver.CHROME);
    }

    @Test
    public void testChatDemo() throws WebElementNotFoundException {
        getDriver().get(getSandboxLocation());
        setTestName("testChatDemo");
        takeScreenshot();

        click("ChatDemo");
        takeScreenshot();

        WebElement textBox = getByDebugId(DebugId.ChatDemoTextBox);
        WebElement sendButton = getByDebugId(DebugId.ChatDemoSendButton);
        WebElement chatLog = getByDebugId(DebugId.ChatDemoChatLog);

        String testToken = System.currentTimeMillis() + "-" + new Random().nextLong();

        textBox.sendKeys(testToken);
        sendButton.click();
        String fullDebugId = GWT_DEBUG_ID_PREFIX + DebugId.ChatDemoChatLog;

        waitForAttribute(By.id(fullDebugId), "value", Pattern.compile(testToken));
    }

    protected WebElement getByDebugId(DebugId debugId) throws WebElementNotFoundException {
        String fullDebugId = GWT_DEBUG_ID_PREFIX + debugId.name();
        List<WebElement> elements = filterVisible(getDriver().findElements(By.id(fullDebugId)));
        if (elements.size() != 1) {
            throw new WebElementNotFoundException("no element found with gwt debug id " + fullDebugId);
        }
        return elements.get(0);
    }

    @Test
    public void testBugStackOverflow15161741() throws WebElementNotFoundException {
        getDriver().get(getSandboxLocation());
        setTestName("testBugStackOverflow15161741");
        takeScreenshot();

        click("StackOverflow15161741");
        takeScreenshot();
        click("xpath://div[text()='2001 January 1, Monday']");
        takeScreenshot();
        click("xpath://div[text()='12']");
        takeScreenshot();
        // table cell not yet updated! (bug)
        assertNotNull(findElement("xpath://div[text()='2001 January 1, Monday']"));
        click("xpath://div[text()='123 Fourth Avenue']");
        takeScreenshot();
        // now it is updated:
        waitForElement("xpath://div[text()='2001 January 12, Friday']");
    }

    @Test
    public void testBug2StackOverflow15161741() throws WebElementNotFoundException, InterruptedException {
        getDriver().get(getSandboxLocation());
        setTestName("testBug2StackOverflow15161741");
        takeScreenshot();

        WebElement el = click("StackOverflow15161741");
        LOG.info("active element: " + activeElement().getText());
        takeScreenshot();
        click("xpath://div[text()='123 Fourth Avenue']");
        takeScreenshot();
        LOG.info("active element: " + activeElement().getText());
        LOG.info("active element: " + getXPathForActiveElement());
        assertEquals("123 Fourth Avenue", activeElement().getText());
        findElement("xpath://tr/td/div[text()='123 Fourth Avenue']").sendKeys(Keys.DOWN);
        takeScreenshot();
        LOG.info("active element 2: " + activeElement().getText());
        LOG.info("active element 2: " + getXPathForActiveElement());
        assertEquals("22 Lance Ln", activeElement().getText());
        findElement("xpath://tr/td/div[text()='22 Lance Ln']").sendKeys(Keys.DOWN);
        takeScreenshot();
        LOG.info("active element 3: " + activeElement().getText());
        LOG.info("active element 3: " + getXPathForActiveElement());
        assertEquals("1600 Pennsylvania Avenue", activeElement().getText());
        click("xpath://div[text()='2001 January 1, Monday']");
        takeScreenshot();
        click("xpath://div[text()='123 Fourth Avenue']");
        takeScreenshot();
        assertEquals("123 Fourth Avenue", activeElement().getText());
        findElement("xpath://tr/td/div[text()='123 Fourth Avenue']").sendKeys(Keys.DOWN);
        findElement("xpath://tr/td/div[text()='123 Fourth Avenue']").sendKeys(Keys.DOWN);
        findElement("xpath://tr/td/div[text()='123 Fourth Avenue']").sendKeys(Keys.DOWN);
        takeScreenshot();
        LOG.info("active element 2: " + activeElement().getText());
        LOG.info("active element 2: " + getXPathForActiveElement());
        // bug: leaving DatePicker dialog by clicking on a table row disables keyboard navigation:
        assertEquals("123 Fourth Avenue", activeElement().getText());
    }

    @Test
    public void testRpcDemo() throws WebElementNotFoundException {
        getDriver().get(getSandboxLocation());
        setTestName("testRpcDemo");
        takeScreenshot();

        click("RpcDemo");
        takeScreenshot();
        List<WebElement> inputs = filterVisible(getDriver().findElements(By.tagName("input")));
        assertEquals(2, inputs.size());
        List<WebElement> buttons = filterVisible(getDriver().findElements(By.tagName("button")));
        assertEquals(6, buttons.size());
        inputs.get(0).sendKeys("abc\n");
        takeScreenshot();
        buttons.get(2).click();
        waitForElement("Received reply from server: Hello unauthenticated user!");
        takeScreenshot();
    }

    @Test
    public void testSimpleEventBusDemo() throws WebElementNotFoundException {
        getDriver().get(getSandboxLocation());
        setTestName("testSimpleEventBusDemo");
        takeScreenshot();

        click("SimpleEventBusDemo");
        takeScreenshot();
        List<WebElement> inputs = filterVisible(getDriver().findElements(By.tagName("input")));
        assertEquals(3, inputs.size());
        inputs.get(0).sendKeys("123\n");
        takeScreenshot();
        assertEquals("123", inputs.get(1).getAttribute("value"));
        assertEquals("123", inputs.get(2).getAttribute("value"));
        inputs.get(1).sendKeys("4\n");
        takeScreenshot();
        assertEquals("1234", inputs.get(2).getAttribute("value"));
        inputs.get(2).sendKeys("5\n");
        takeScreenshot();
        assertEquals("12345", inputs.get(1).getAttribute("value"));
    }

    @Test
    public void testHandlerManagerDemo() throws WebElementNotFoundException {
        getDriver().get(getSandboxLocation());
        setTestName("testHandlerManagerDemo");
        takeScreenshot();

        click("HandlerManagerDemo");
        takeScreenshot();
        List<WebElement> inputs = filterVisible(getDriver().findElements(By.tagName("input")));
        assertEquals(3, inputs.size());
        inputs.get(0).sendKeys("123\n");
        takeScreenshot();
        assertEquals("123", inputs.get(1).getAttribute("value"));
        assertEquals("123", inputs.get(2).getAttribute("value"));
        inputs.get(1).sendKeys("4\n");
        takeScreenshot();
        assertEquals("1234", inputs.get(2).getAttribute("value"));
        inputs.get(2).sendKeys("5\n");
        takeScreenshot();
        assertEquals("12345", inputs.get(1).getAttribute("value"));
    }

    public List<WebElement> filterVisible(List<WebElement> elements) {
        List<WebElement> result = new ArrayList<>();
        for (WebElement element : elements) {
            if (element.isDisplayed()) {
                result.add(element);
            }
        }
        return result;
    }
}
