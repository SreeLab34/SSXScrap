package demo.utils;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ActionsWrapper {
   
    public static void wrapperClick(WebDriver driver, By locator) {
        try {
            WSUtils.logStatus("wrapperClick", "Click on the locator");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            WebElement element = driver.findElement(locator);
            element.click();
        } catch (Exception e) {
            System.out.println("Exception while clicking");        }
    }
}