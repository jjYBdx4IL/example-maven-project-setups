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
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.rpcdemo.RpcDemo;
import com.github.jjYBdx4IL.test.selenium.SeleniumTestBase;
import com.github.jjYBdx4IL.test.selenium.WebElementNotFoundException;
import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeNotNull;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeleniumTest extends SeleniumTestBase {

    private static final Logger LOG = LoggerFactory.getLogger(SeleniumTest.class);
    private static final File DEVMODE_INSTALL_DIR;
    
    static {
        String s = System.getProperty("devmode.install.dir", null);
        LOG.info(s);
        DEVMODE_INSTALL_DIR = s == null ? null : new File(s);
    }
    
    /**
     *
     * @return some URL string pointing to the location of the app to be tested
     */
    public String getSandboxLocation() {
        String location = System.getProperty("sandbox.location");
        LOG.debug("sandbox location = " + location);
        assertNotNull(location);
        assertTrue(!location.isEmpty());
        return location;
    }

    @Before
    public void before() {
        getDriver(SeleniumTestBase.Driver.CHROME);
    }
    
    @Test
    public void testGWTDevModeRefreshOnBrowserReload() throws IOException, WebElementNotFoundException {
        assumeNotNull(DEVMODE_INSTALL_DIR);
        
        getDriver().get(getSandboxLocation());
        click("RpcDemo");
        WebElement listBox = getByDebugId(DebugId.RpcDemoListBox);
        Select sel = new Select(listBox);
        WebElement firstOption = sel.getOptions().get(0);
        assertEquals("first listbox entry", firstOption.getText());
        
        // update the text for the first listbox entry
        File classFile = new File(DEVMODE_INSTALL_DIR, "client/src/main/java/" + RpcDemo.class.getName().replace(".", "/") + ".java");
        String classContents = FileUtils.readFileToString(classFile, "UTF-8");
        String updatedClassContents = classContents.replace("\"first listbox entry\"", "\"first listbox entry updated\"");
        FileUtils.write(classFile, updatedClassContents, "UTF-8");
        
        // refresh browser page and check
        getDriver().get(getSandboxLocation());
        click("RpcDemo");
        listBox = getByDebugId(DebugId.RpcDemoListBox);
        sel = new Select(listBox);
        firstOption = sel.getOptions().get(0);
        assertEquals("first listbox entry updated", firstOption.getText());
        
        // undo
        FileUtils.write(classFile, classContents, "UTF-8");
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
        LOG.info("chat log contents: " + chatLog.getAttribute("value"));
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
        List<WebElement> inputs = filterDisplayed(getDriver().findElements(By.tagName("input")));
        assertEquals(2, inputs.size());
        List<WebElement> buttons = filterDisplayed(getDriver().findElements(By.tagName("button")));
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
        List<WebElement> inputs = filterDisplayed(getDriver().findElements(By.tagName("input")));
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
        getByDebugId(DebugId.HandlerManagerDemo);
        takeScreenshot();
        List<WebElement> inputs = filterDisplayed(getDriver().findElements(By.tagName("input")));
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

    public List<WebElement> filterDisplayed(List<WebElement> elements) {
        List<WebElement> result = new ArrayList<>();
        for (WebElement element : elements) {
            if (element.isDisplayed()) {
                result.add(element);
            }
        }
        return result;
    }
    
    protected WebElement getByDebugId(DebugId debugId) throws WebElementNotFoundException {
        String fullDebugId = GWT_DEBUG_ID_PREFIX + debugId.name();
        List<WebElement> elements = filterDisplayed(getDriver().findElements(By.id(fullDebugId)));
        if (elements.size() != 1) {
            throw new WebElementNotFoundException("no element found with gwt debug id " + fullDebugId);
        }
        return elements.get(0);
    }
}
