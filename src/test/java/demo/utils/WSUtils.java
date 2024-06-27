package demo.utils;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class WSUtils {
    
    public static void logStatus(String methodName, String message) {
        System.out.println(methodName +" "+ message);
    } 
    public static void getURL(WebDriver driver, String url) {
        try {
            logStatus("getURL", "Navigate to that website");
            driver.get(url);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.jsReturnsValue("return document.readyState == 'complete'"));
        } catch (Exception e) {
        System.out.println("Exception occured");
        }
    }
    public static ArrayList<HashMap<String, Object>> retrieveInfo(WebDriver driver, int pages, String operator, double pAge) {
        logStatus("retrieveInfo", String.format("Getting data with win pAge %s %.2f%%", operator, pAge));
        ArrayList<HashMap<String, Object>> data = new ArrayList<>();
        try {
            // Loop through specified number of pages
            for (int page=1; page<=pages; page++) {
                // Navigate to the page
                getURL(driver, "https://www.scrapethissite.com/pages/forms/?page_num=" + page);

                // Find the table containing the data
                WebElement table = driver.findElement(By.xpath("//table[contains(@class, 'table')]"));

                // Find rows matching win pAge criteria
                List<WebElement> tableRow = table.findElements(By.xpath(".//td[contains(@class, 'pct')][text() "+ operator +" "+ (pAge/100) +"]/parent::tr"));

                // Iterate through each matching row
                for (WebElement cell : tableRow) {
                    // Extract data from table cells
                    String epochTime = String.valueOf(getEpochTime());
                    String name = cell.findElement(By.xpath("./td[contains(@class, 'name')]")).getText();
                    String year = cell.findElement(By.xpath("./td[contains(@class, 'year')]")).getText();
                    String pct = cell.findElement(By.xpath("./td[contains(@class, 'pct')]")).getText();

                    // Create hashmap to store data for current row
                    HashMap<String, Object> hm = new HashMap<>();
                    hm.put("epochTime", epochTime);
                    hm.put("name", name);
                    hm.put("year", year);
                    hm.put("pct", pct);

                    // Add hashmap to list of data
                    data.add(hm);
                }
            }
        } catch (Exception e) {
        System.out.println("Exception occured");
        }
        return data;
    }
    public static void dataToJson(ArrayList<HashMap<String, Object>> data, String outputDirectory, String fileName) {
        try {
            logStatus("dataToJson", "Converting and storing ArrayList to JSON");

            // Create ObjectMapper instance
            ObjectMapper om = new ObjectMapper();
            om.enable(SerializationFeature.INDENT_OUTPUT);

            // Create output directory if it doesn't exist
            File outputDir = new File(outputDirectory);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            // Create output file
            File outputFile = new File(outputDir, fileName);

            // Write data to JSON file
            om.writeValue(outputFile, data);
        } catch (Exception e) {
        System.out.println("Exception occured");
        }
    }
    private static long getEpochTime() {
        return System.currentTimeMillis()/1000;
    }
    public static ArrayList<HashMap<String, Object>> getTopNOscarOfEachYear(WebDriver driver, int topOscar) {
        logStatus("getTopNOscarOfEachYear", String.format("Getting data with top %s oscars", topOscar));

        // Initialize list to store scraped data
        ArrayList<HashMap<String, Object>> data = new ArrayList<>();
        try {
            // Find elements containing year information
            List<WebElement> years = driver.findElements(By.xpath("//a[contains(@class, 'year-link')]"));

            // Set implicit wait time
            driver.manage().timeouts().implicitlyWait(Duration.ofMillis(1));

            // Iterate through each year element
            for (WebElement year : years) {
                // Click on the year element
                year.click();
                FluentWait<WebDriver> fWait = new FluentWait<WebDriver>(driver)
                                            .withTimeout((Duration.ofSeconds(10)))
                                            .pollingEvery(Duration.ofMillis(250));
                fWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table")));

                // Find table containing data
                WebElement table = driver.findElement(By.xpath("//table[contains(@class, 'table')]"));

                // Find top N table rows for each year
                List<WebElement> tableRows = table.findElements(By.xpath(".//tr[contains(@class, 'film')][position() <= "+ topOscar +"]"));

                // Iterate through each table row
                for (WebElement cell : tableRows) {
                    // Extract data from table cells
                    String epochTime = String.valueOf(getEpochTime());
                    String yr = year.getText();
                    String title = cell.findElement(By.xpath("./td[contains(@class, 'title')]")).getText();
                    String nomination = cell.findElement(By.xpath("./td[contains(@class, 'nomination')]")).getText();
                    String awards = cell.findElement(By.xpath("./td[contains(@class, 'awards')]")).getText();
                    Boolean isWinner = false;
                    try {
                        isWinner = cell.findElement(By.xpath("./td[contains(@class, 'best-picture')]/i")).isDisplayed();
                    } catch (Exception e) {}

                    // Create hashmap to store data for current row
                    HashMap<String, Object> hm = new HashMap<>();
                    hm.put("epochTime", epochTime);
                    hm.put("year", yr);
                    hm.put("title", title);
                    hm.put("nomination", nomination);
                    hm.put("awards", awards);
                    hm.put("isWinner", isWinner);

                    // Add hashmap to list of data
                    data.add(hm);
                }
            }
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        } catch (Exception e) {
        System.out.println("Exception occured");
        }
        return data;
    }
}