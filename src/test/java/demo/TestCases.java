package demo;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.time.Duration;
import java.util.logging.Level;

import demo.utils.ExcelDataProvider;
// import io.github.bonigarcia.wdm.WebDriverManager;
import demo.wrappers.Wrappers;

public class TestCases extends ExcelDataProvider { // Lets us read the data
        private WebDriver driver;
        private Wrappers wrappers;
        //SoftAssert softAssert;

        /*
         * TODO: Write your tests here with testng @Test annotation.
         * Follow `testCase01` `testCase02`... format or what is provided in
         * instructions
         */

        /*
         * Do not change the provided methods unless necessary, they will help in
         * automation and assessment
         */

        @BeforeMethod
        public void openUrl() {
                // Open youtube before every method that is test cases
                driver.get("https://www.youtube.com/");
                String expectedUrl = "https://www.youtube.com/";
                String currentUrl = driver.getCurrentUrl();
                Assert.assertEquals(currentUrl, expectedUrl, "Navigation to the URL failed.");
        }

        @BeforeTest
        public void startBrowser() {
                System.setProperty("java.util.logging.config.file", "logging.properties");

                // NOT NEEDED FOR SELENIUM MANAGER
                // WebDriverManager.chromedriver().timeout(30).setup();

                ChromeOptions options = new ChromeOptions();
                LoggingPreferences logs = new LoggingPreferences();

                logs.enable(LogType.BROWSER, Level.ALL);
                logs.enable(LogType.DRIVER, Level.ALL);
                options.setCapability("goog:loggingPrefs", logs);
                options.addArguments("--remote-allow-origins=*");

                System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "build/chromedriver.log");

                driver = new ChromeDriver(options);
                //Initializing wrappers with waits inculcation.
                wrappers = new Wrappers((ChromeDriver) driver, Duration.ofSeconds(10));

                driver.manage().window().maximize();
        }

        @Test(priority = 1, description = "Assert you are on the correct URL. Click on 'About' at the bottom of the sidebar, and print the message on the screen.")
        public void testCase01() throws InterruptedException {
                System.out.println("Start testCase01");
                wrappers.clickElement(By.xpath("//a[contains(text(),'About')]"));
                wrappers.printMessage();
                System.out.println("End testCase01");
        }

        @Test(priority = 2, description = "Apply a Soft Assert on whether the movie is marked “A” for Mature or not. Apply a Soft assert on the movie category to check if it exists ex: \"Comedy\", \"Animation\", \"Drama\".")
        public void testCase02() throws InterruptedException {
                System.out.println("Start testCase02");
                wrappers.clickOnTab("Films");
                wrappers.scrollToRight("Top selling");
                wrappers.maturityLevel();
                wrappers.genreOfLastMovie();
                System.out.println("End testCase02");
        }

        @Test(priority = 3, description = "Print the name of the playlist. Soft Assert on whether the number of tracks listed is less than or equal to 50.")
        public void testCase03() throws InterruptedException {
                System.out.println("Start testCase03");
                wrappers.clickOnTab("Music");
                wrappers.scrollToRight("Biggest Hits");
                wrappers.noOfTracks("Biggest Hits", "Bollywood Dance");
                System.out.println("End testCase03");
        }

        @Test(priority = 4, description = "Go to the 'News' tab and print the title and body of the 1st 3 “Latest News Posts” along with the sum of the number of likes on all 3 of them. No likes given means 0.")
        public void testCase04() throws InterruptedException {
                System.out.println("Start testCase04");
                wrappers.clickOnTab("News");
                wrappers.titleOfNews();
                wrappers.sumOfTheLikes();
                System.out.println("End testCase04");
        }

        //Search for each of the items given in the stubs: src/test/resources/data.xlsx, and keep scrolling till the sum of each videos views reach 10 Cr.
        //this was guided by mentor so need to take help from google that in java how the mathematical logic will get implemented here.
        @Test(priority = 5, dataProvider = "excelData", dataProviderClass = ExcelDataProvider.class)
        public void testCase05(String to_be_searched) throws InterruptedException {
                System.out.println("Start testCase05");
                wrappers.click(By.xpath("//input[@placeholder='Search']"));
                wrappers.sendKeys(By.xpath("//input[@placeholder='Search']"), to_be_searched);

                wrappers.click(By.id("search-icon-legacy"));
                Thread.sleep(5000);

                long totalViews = 0;
                while (totalViews < 1000000000) { // 10 Crore views conversion
                        List<WebElement> videoElements = driver.findElements(By.xpath(
                                        "//span[contains(@class,'inline-metadata') and contains(text(),'views')]"));

                        for (WebElement videoElement : videoElements) {
                                String viewsText = videoElement.getText();
                                if (viewsText.contains("views")) {
                                        viewsText = viewsText.split(" ")[0];
                                        totalViews += parseViews(viewsText);
                                }

                                if (totalViews >= 1000000000) {
                                        break;
                                }
                        }

                        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 1000);");
                        Thread.sleep(2000); // Wait for the new videos to be loaded.
                }

                System.out.println("Total views for " + to_be_searched + ": " + totalViews);
                System.out.println("End testCase05");
        }

        private long parseViews(String viewsText) {
                long views = 0;
                if (viewsText.endsWith("K")) {
                        views = (long) (Double.parseDouble(viewsText.replace("K", "")) * 1_000);
                } else if (viewsText.endsWith("M")) {
                        views = (long) (Double.parseDouble(viewsText.replace("M", "")) * 1_000_000);
                } else if (viewsText.endsWith("B")) {
                        views = (long) (Double.parseDouble(viewsText.replace("B", "")) * 1_000_000_000);
                } else {
                        views = Long.parseLong(viewsText.replace(",", ""));
                }
                return views;
        }

        @DataProvider(name = "excelData")
        public static Object[][] provideData() {
                return new Object[][] {
                                { "Movies" },
                                { "Music" },
                                { "Games" }
                };
        }

        @AfterTest
        public void endTest() {
                driver.close();
                driver.quit();

        }
}