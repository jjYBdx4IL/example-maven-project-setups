package my.example;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import io.github.bonigarcia.wdm.WebDriverManager;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.function.Function;

public class ExampleSeleniumIT {

    static {
        // fetch chrome driver executable 
    	WebDriverManager.chromedriver().setup();
    }
    
    static ChromeDriver driver = null;

    @BeforeClass
    public static void beforeClass() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--disable-gpu");
        chromeOptions.addArguments("--dbus-stub");
        driver = new ChromeDriver(chromeOptions);
    }

    @Test
    public void test() throws InterruptedException {
        driver.get("http://localhost:8080");
        String pageSource = driver.getPageSource();
        System.out.println(pageSource);
        assertTrue(pageSource.contains("Any title is fine"));
        
        WebElement sendButton = getElement("//button[text()='Send']");
        Thread.sleep(1000);
        new Actions(driver).moveToElement(sendButton).click().perform();
        
        WebElement closeButton = getElement("//button[text()='Close']");
        Thread.sleep(1000);
        new Actions(driver).moveToElement(closeButton).click().perform();
        
        WebElement textbox = getElement("//input");
        textbox.clear();
        new Actions(driver).moveToElement(sendButton).click().perform();
        assertNotNull(getElement("//div[text()='Please enter at least four characters']"));
        
        Thread.sleep(1000);
        textbox.sendKeys("some user name");
        new Actions(driver).moveToElement(sendButton).click().perform();
        assertNotNull(getElement("//div[text()='Hello, some user name!']"));
        Thread.sleep(1000);
    }
    
    WebElement getElement(String xpath) {
        return new WebDriverWait(driver, 10).until(new Function<WebDriver, WebElement>() {
            @Override
            public WebElement apply(WebDriver t) {
                return driver.findElementByXPath(xpath);
            }
        });
    }
    
    @AfterClass
    public static void afterClass() {
        driver.quit();
    }

}
