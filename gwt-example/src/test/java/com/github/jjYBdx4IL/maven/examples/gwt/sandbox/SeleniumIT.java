package com.github.jjYBdx4IL.maven.examples.gwt.sandbox;

import com.github.jjYBdx4IL.test.selenium.SeleniumTestBase;
import com.github.jjYBdx4IL.test.selenium.WebElementNotFoundException;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

// CHECKSTYLE IGNORE MagicNumber FOR NEXT 10000 LINES
public class SeleniumIT extends SeleniumTestBase {

    private static final Logger log = Logger.getLogger(SeleniumIT.class.getName());

    private String getSandboxLocation() {
        String location = System.getProperty("sandbox.location");
        log.debug("sandbox location = " + location);
        assertNotNull(location);
        assertTrue(!location.isEmpty());
        return location;
    }

    @BeforeClass
    public static void initDriver() {
        getDriver(Driver.CHROME);
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
    public void testBug2StackOverflow15161741() throws WebElementNotFoundException {
        getDriver().get(getSandboxLocation());
        setTestName("testBug2StackOverflow15161741");
        takeScreenshot();

        click("StackOverflow15161741");
        takeScreenshot(); // #628cd5
        click("xpath://div[text()='123 Fourth Avenue']");
        takeScreenshot();
        log.info("active element: " + activeElement().getText());
        log.info("active element: " + getXPathForActiveElement());
        assertEquals("123 Fourth Avenue", activeElement().getText());
        findElement("xpath://tr/td/div[text()='123 Fourth Avenue']").sendKeys(Keys.DOWN);
        takeScreenshot();
        log.info("active element 2: " + activeElement().getText());
        log.info("active element 2: " + getXPathForActiveElement());
        assertEquals("22 Lance Ln", activeElement().getText());
        findElement("xpath://tr/td/div[text()='22 Lance Ln']").sendKeys(Keys.DOWN);
        takeScreenshot();
        log.info("active element 3: " + activeElement().getText());
        log.info("active element 3: " + getXPathForActiveElement());
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
        log.info("active element 2: " + activeElement().getText());
        log.info("active element 2: " + getXPathForActiveElement());
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
        assertEquals(4, buttons.size());
        inputs.get(0).sendKeys("abc\n");
        takeScreenshot();
        buttons.get(0).click();
        waitForElement("Received reply from server: Hello ABC!");
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
