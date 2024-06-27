package demo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.openqa.selenium.By;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;
import java.util.logging.Level;
import org.openqa.selenium.logging.LogType;
import demo.utils.ActionsWrapper;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import demo.utils.WSUtils;

public class TestCases {
    WebDriver driver;

    @BeforeTest
    public void startBrowser() {
        WSUtils.logStatus("startBrowser", "Initializing browser and driver method");

        System.setProperty("java.util.logging.config.file", "logging.properties");
        ChromeOptions options = new ChromeOptions();
        LoggingPreferences logs = new LoggingPreferences();
        logs.enable(LogType.BROWSER, Level.ALL);
        logs.enable(LogType.DRIVER, Level.ALL);
        options.setCapability("goog:loggingPrefs", logs);
        options.addArguments("--remote-allow-origins=*");
        System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "build/chromedriver.log");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        WSUtils.logStatus("startBrowser", "Successfull");

    }

    @AfterTest
    public void endTest()
    {
        WSUtils.logStatus("endTest", "EndTest Successfull");
        if (driver != null) {
            driver.quit();
        }
        WSUtils.logStatus("endTest", "Quitted Successfully");
    }

    @BeforeMethod
    public void getURL() {
        String url = "https://www.scrapethissite.com/pages";
        WSUtils.getURL(driver, url);
    }

    @Test(priority = 1, enabled = true, description = "testcase 01")
    public void testCase01() {
        WSUtils.logStatus("TC001", "Start test case 01");

        // Click on "Hockey Teams"
        ActionsWrapper.wrapperClick(driver, By.xpath("//a[contains(text(), 'Hockey Teams')]"));
        ArrayList<HashMap<String, Object>> data = WSUtils.retrieveInfo(driver, 4, "<", 40);
        String outputDirectory = "output";
        String fileName = "hockey-team-data.json";
        WSUtils.dataToJson(data, outputDirectory, fileName);
        File file = new File(outputDirectory + File.separator + fileName);
        Assert.assertTrue(file.exists(), String.format("'%s' file does not exist", fileName));
        WSUtils.logStatus("Step", String.format("'%s' file exists", fileName));
        Assert.assertTrue(file.length() > 0, String.format("'%s' file is empty", fileName));
        WSUtils.logStatus("Step", String.format("'%s' file is not empty", fileName));

        WSUtils.logStatus("TC001", "End of test case 01");
    }

    @Test(priority = 2, enabled = true, description = "test case 02")
    public void testCase02() {
        WSUtils.logStatus("TC002", "Start of test case 02");
        //click on oscar winning films
        ActionsWrapper.wrapperClick(driver, By.xpath("//a[contains(text(), 'Oscar Winning Films')]"));
        ArrayList<HashMap<String, Object>> data = WSUtils.getTopNOscarOfEachYear(driver, 5);
        String outputDirectory = "output";
        String fileName = "oscar-winner-data.json";
        WSUtils.dataToJson(data, outputDirectory, fileName);
        File file = new File(outputDirectory + File.separator + fileName);
        Assert.assertTrue(file.exists(), String.format("'%s' file does not exist", fileName));
        Assert.assertTrue(file.length() > 0, String.format("'%s' file is empty", fileName));
        WSUtils.logStatus("testCase02", "End of tst case 2");
    }
}