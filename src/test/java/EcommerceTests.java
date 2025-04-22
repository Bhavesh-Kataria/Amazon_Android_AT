import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.URL;
import java.time.Duration;
import java.util.Properties;
import java.util.Set;

public class EcommerceTests {
    public static AndroidDriver driver;
    public static DesiredCapabilities dcs;
    public static Properties prop;

    @BeforeClass
    public void setUp(){
        dcs = new DesiredCapabilities();
        dcs.setCapability(MobileCapabilityType.DEVICE_NAME, "emulator-5554");
        dcs.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
        dcs.setCapability(MobileCapabilityType.PLATFORM_VERSION, "9.0");
        dcs.setCapability(MobileCapabilityType.BROWSER_NAME, "chrome");
        dcs.setCapability(MobileCapabilityType.AUTOMATION_NAME, "UiAutomator2");
        dcs.setCapability(MobileCapabilityType.NO_RESET,"true");
        dcs.setCapability("chromedriverExecutable","/Users/topb/Documents/chromedriver");
        dcs.setCapability("adbExecTimeout", 30000); // Increase timeout to 30 seconds (default is 20s)
        dcs.setCapability("newCommandTimeout", 600);  // Increase timeout for commands
        dcs.setCapability("uiautomator2ServerInstallTimeout", 120000);
        dcs.setCapability("uiautomator2ServerLaunchTimeout", 120000);
        try{
            FileInputStream fis = new FileInputStream("/Users/topb/Desktop/Automation Testing/App Testing/EcommerceAT/src/main/config/config.properties");
            prop = new Properties();
            prop.load(fis);
            driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"),dcs);
            driver.get(prop.getProperty("url"));
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    @Test
    public void logInTest(){
        driver.findElement(By.id("nav-link-accountList-nav-line-1")).click();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.findElement(By.id("ap_email_login")).sendKeys(prop.getProperty("number"));
        driver.findElement(By.xpath("//input[@type='submit']")).click();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.findElement(By.id("ap_password")).sendKeys(prop.getProperty("password"));
        driver.findElement(By.id("signInSubmit")).click();
        try{
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        driver.findElement(By.id("auth-signin-button")).click();
        String url = driver.getCurrentUrl();
        Assert.assertEquals(url,"https://www.amazon.in/?ref_=nav_ya_signin","Invalid url");
    }

    @Test
    public void searchTest(){
        driver.findElement(By.id("twotabsearchtextbox")).sendKeys("davidoff coffee");
        driver.findElement(By.id("nav-search-submit-button")).click();
        List<WebElement> elements = driver.findElements(By.xpath("//span[contains(text(),'Davidoff Origins Brazil Flavour')]"));
        elements.get(3).click();
        Set<String> windowHandles = driver.getWindowHandles();
        String originalWindowHandle = driver.getWindowHandle();
        for (String handle : windowHandles) {
            if (!handle.equals(originalWindowHandle)) {
                driver.switchTo().window(handle); // Switch to the new tab
                break; // No need to continue once we switch
            }
        }
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        String partialTitle = "Davidoff Origins Brazil Flavour Instant Coffee";
        String title = driver.getTitle();
        Assert.assertTrue(title.startsWith(partialTitle),"Invalid Product Page Opened");
    }

    @Test
    public void addToCart(){
        driver.findElement(By.id("add-to-cart-button")).click();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.findElement(By.partialLinkText("Go to Cart")).click();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        String cartText = driver.findElement(By.id("sc-subtotal-label-activecart")).getText();
        Assert.assertEquals(cartText,"Subtotal (1 item)","Item status incorrect");
    }

    @Test
    public void gotoLaptopCategoryTest(){
        driver.findElement(By.id("nav-hamburger-menu")).click();
        driver.findElement(By.xpath("//div[contains(text(),'Mobiles, Computers')]/parent::a")).click();
        driver.findElement(By.partialLinkText("Laptops")).click();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.findElement(By.xpath("//a//h2//span[contains(text(),'Apple MacBook Air Laptop: Apple M1 chip')]")).click();
    }

    @Test
    public void addToWishlist(){
        driver.findElement(By.id("wishListDropDown")).click();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.findElement(By.id("atwl-list-name-2D7JW6VREOQYZ")).click();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
    }
}
