/*
 * Copyright © 2016 jjYBdx4IL (https://github.com/jjYBdx4IL)
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
package com.github.jjYBdx4IL.maven.examples.gwt.sandbox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNotNull;

import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.api.DebugId;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.rpcdemo.RpcDemo;
import com.github.jjYBdx4IL.maven.examples.server.GWTServiceImpl;
import com.github.jjYBdx4IL.test.selenium.SeleniumTestBase;
import com.github.jjYBdx4IL.test.selenium.WebElementNotFoundException;

import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.DirectoryScanner;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Pattern;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import io.github.bonigarcia.wdm.WebDriverManager;

public class SeleniumTest extends SeleniumTestBase {

    private static final Logger LOG = LoggerFactory.getLogger(SeleniumTest.class);
    private static final File DEVMODE_INSTALL_DIR;

    static {
        String s = System.getProperty("devmode.install.dir", null);
        LOG.info(s);
        DEVMODE_INSTALL_DIR = s == null ? null : new File(s);
        
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--disable-gpu");
        chromeOptions.addArguments("--dbus-stub");
        setDriver(new ChromeDriver(chromeOptions));
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
    public void before() throws InterruptedException {
    }

    @Test
    public void testJpaCellTableDemo() throws WebElementNotFoundException {
        openPage();
        setTestName("testJpaCellTableDemo");
        takeScreenshot();

        click(String.format(Locale.ROOT, "xpath://*[@id='%s']", getDebugId(DebugId.JpaCellTableExample)));
        takeScreenshot();

        WebElement data1Input = getByDebugId(DebugId.JpaCellTableExampleData1TextBox);
        String testToken = System.currentTimeMillis() + "-" + new Random().nextLong();
        data1Input.sendKeys(testToken);

        WebElement sendButton = getByDebugId(DebugId.JpaCellTableExampleAddButton);
        sendButton.click();

        // wait for list update from server
        assertNotNull(waitForElement(String.format(Locale.ROOT, "xpath://*[@id='%s']//*[contains(text(),'%s')]",
                getDebugId(DebugId.JpaCellTableExampleTable),
                testToken)));
        takeScreenshot();
    }

    @Test
    public void testJettyDevWebAppAutoReload() throws Exception {
        assumeNotNull(DEVMODE_INSTALL_DIR);

        File sourceFile = new File(DEVMODE_INSTALL_DIR,
                "server/src/main/java/" + GWTServiceImpl.class.getName().replace(".", "/") + ".java");
        String unauthReply = "Hello unauthenticated user!";
        String changedReply = "Hello xyz user!";

        openPage();
        click("RpcDemo");
        WebElement greetMeButton = getByDebugId(DebugId.RpcDemoGreetMeButton);
        greetMeButton.click();

        // wait for reply from server
        assertNotNull(waitForElement(String.format(Locale.ROOT, "xpath://*[@id='%s'][contains(text(),'%s')]",
                getDebugId(DebugId.RpcDemoReplyLabel),
                unauthReply)));

        // recompile server-side service class to send another reply
        String sourceContents = FileUtils.readFileToString(sourceFile, "UTF-8");
        String updatedSourceContents = sourceContents.replace(unauthReply, changedReply);
        try {
            FileUtils.write(sourceFile, updatedSourceContents, "UTF-8");
            recompile(sourceFile, findWebInfClassesDir(new File(DEVMODE_INSTALL_DIR, "server/target")));
            Thread.sleep(10000L); // give the server enough time to reload

            greetMeButton.click();

            // wait for modified reply from server
            assertNotNull(waitForElement(String.format(Locale.ROOT, "xpath://*[@id='%s'][contains(text(),'%s')]",
                    getDebugId(DebugId.RpcDemoReplyLabel),
                    changedReply)));
        } finally {
            // undo
            FileUtils.write(sourceFile, sourceContents, "UTF-8");
            recompile(sourceFile, findWebInfClassesDir(new File(DEVMODE_INSTALL_DIR, "server/target")));
            Thread.sleep(10000L); // give the server enough time to reload
        }
    }

    @Test
    public void testGWTDevModeRefreshOnBrowserReload() throws IOException, WebElementNotFoundException {
        assumeNotNull(DEVMODE_INSTALL_DIR);

        openPage();
        click("RpcDemo");
        WebElement listBox = getByDebugId(DebugId.RpcDemoListBox);
        Select sel = new Select(listBox);
        WebElement firstOption = sel.getOptions().get(0);
        assertEquals("first listbox entry", firstOption.getText());

        // update the text for the first listbox entry
        File sourceFile = new File(DEVMODE_INSTALL_DIR, "client/src/main/java/" + RpcDemo.class.getName().replace(".", "/") + ".java");
        String sourceContents = FileUtils.readFileToString(sourceFile, "UTF-8");
        String updatedSourceContents = sourceContents.replace("\"first listbox entry\"", "\"first listbox entry updated\"");
        try {
            FileUtils.write(sourceFile, updatedSourceContents, "UTF-8");

            // refresh browser page and check
            openPage();
            click("RpcDemo");
            listBox = getByDebugId(DebugId.RpcDemoListBox);
            sel = new Select(listBox);
            firstOption = sel.getOptions().get(0);
            assertEquals("first listbox entry updated", firstOption.getText());
        } finally {
            // undo
            FileUtils.write(sourceFile, sourceContents, "UTF-8");
        }
    }

    @Test
    public void testChatDemo() throws WebElementNotFoundException {
        openPage();
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
        openPage();
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
        openPage();
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
        openPage();
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
    public void testSimpleEventBusDemo() throws WebElementNotFoundException, InterruptedException {
        openPage();
        setTestName("testSimpleEventBusDemo");
        takeScreenshot();

        click("SimpleEventBusDemo");
        takeScreenshot();
        
        List<WebElement> inputs = filterDisplayed(getDriver().findElements(By.tagName("input")));
        int i = 10;
        while (i > 0 && inputs.size() != 3) {
            Thread.sleep(1000L);
            inputs = filterDisplayed(getDriver().findElements(By.tagName("input")));
            i--;
        }
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
        openPage();
        setTestName("testHandlerManagerDemo");
        takeScreenshot();

        click("HandlerManagerDemo");
        getByDebugId(DebugId.HandlerManagerDemoBody);
        takeScreenshot();
        
        List<WebElement> inputs = getElementsByTagName(DebugId.HandlerManagerDemoBody, "input", 3, 3);
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

    protected String getDebugId(DebugId debugId) {
        return GWT_DEBUG_ID_PREFIX + debugId.name();
    }

    protected WebElement getByDebugId(DebugId debugId) throws WebElementNotFoundException {
        try {
            getDriver().manage().timeouts().implicitlyWait(DEFAULT_WAIT_SECS, TimeUnit.SECONDS);
            String fullDebugId = getDebugId(debugId);
            List<WebElement> elements = filterDisplayed(getDriver().findElements(By.id(fullDebugId)));
            if (elements.size() != 1) {
                throw new WebElementNotFoundException("no element found with gwt debug id " + fullDebugId);
            }
            return elements.get(0);
        } finally {
            getDriver().manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        }
    }
    
    protected boolean wait4DebugId(DebugId debugId, int seconds) {
        try {
            getDriver().manage().timeouts().implicitlyWait(seconds, TimeUnit.SECONDS);
            String fullDebugId = getDebugId(debugId);
            List<WebElement> elements = filterDisplayed(getDriver().findElements(By.id(fullDebugId)));
            return !elements.isEmpty();
        } finally {
            getDriver().manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        }
    }

    protected void recompile(File sourceFile, File classesDir) throws Exception {
        LOG.info("compiling " + sourceFile.getAbsolutePath() + " to " + classesDir.getAbsolutePath());

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager jfm = compiler.getStandardFileManager(diagnostics, Locale.getDefault(), Charset.forName("UTF-8"));

        Iterable<? extends JavaFileObject> compilationUnit = jfm.getJavaFileObjects(sourceFile);

        List<String> optionList = new ArrayList<String>();
        optionList.add("-d");
        optionList.add(classesDir.getAbsolutePath()); // classes destination dir
//        optionList.add("-classpath");
//        optionList.add(System.getProperty("java.class.path") + ";dist/InlineCompiler.jar");

        JavaCompiler.CompilationTask task = compiler.getTask(
                null,
                jfm,
                diagnostics,
                optionList,
                null,
                compilationUnit);

        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
            LOG.error("Error on line {} in {}: {}",
                    diagnostic.getLineNumber(),
                    diagnostic.getSource().toUri(),
                    diagnostic.getMessage(Locale.getDefault()));
        }

        assertTrue("compilation failed, source to compile was: "
                + FileUtils.readFileToString(sourceFile, "UTF-8"), task.call());

        jfm.close();
    }

    protected File findWebInfClassesDir(File searchRoot) {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(searchRoot);
        scanner.setIncludes(new String[]{"**/WEB-INF/classes"});
        scanner.scan();
        assertEquals(1, scanner.getIncludedDirsCount());
        return new File(searchRoot, scanner.getIncludedDirectories()[0]);
    }

    /**
     * when running against GWT dev mode, the page sometimes fails to load. this is a workaround.
     * @throws com.github.jjYBdx4IL.test.selenium.WebElementNotFoundException if not found
     */
    protected void openPage() throws WebElementNotFoundException {
        for (int i = 0; i < 10; i++) {
            getDriver().get(getSandboxLocation());
            if (wait4DebugId(DebugId.JpaCellTableExample, 10)) {
                return;
            }
        }
        throw new WebElementNotFoundException();
    }
    
    /**
     * Wait for a specific number of elements below parentElementId and having tag tagName and return them.
     * 
     * @param parentElementId the parent element below which to look for the child elements. Not null.
     * @param tagName the child elements' tag name, ie. 'input' or 'div'. Can be null.
     * @param minCount supply non-positive value to not check for min count limit
     * @param maxCount supply negative value to not check for max count limit
     * @return list of matching elements
     */
    public List<WebElement> getElementsByTagName(DebugId parentElementId, String tagName, final int minCount, final int maxCount) {
        assertNotNull(tagName);
        final List<WebElement> elements = new ArrayList<>();
        String fullDebugId = getDebugId(parentElementId);
        final By by = parentElementId != null
                ? By.xpath(String.format(Locale.ROOT, "//*[@id='%s']//%s", fullDebugId, tagName))
                : By.xpath(String.format(Locale.ROOT, "//%s", tagName));
        final int _maxCount = maxCount >= 0 ? maxCount : Integer.MAX_VALUE;
        LOG.info(String.format(Locale.ROOT, "waiting for %d to %d elements with tag '%s' below element with id '%s'",
                minCount, _maxCount, tagName, fullDebugId));
        waitUntil(new Function<WebDriver, Boolean>(){
            @Override
            public Boolean apply(WebDriver t) {
                elements.clear();
                elements.addAll(filterDisplayed(t.findElements(by)));
                return elements.size() <= _maxCount && elements.size() >= minCount;
            }
        });
        return elements;
    }
}
