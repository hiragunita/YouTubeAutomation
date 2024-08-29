package demo.wrappers;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

public class Wrappers {

    private WebDriver driver;
    private WebDriverWait wait;

    public Wrappers(WebDriver driver, Duration timeout){
        this.driver = driver;
        this.wait = new WebDriverWait(driver, timeout);
    }

    public void clickElement(By xpath){
        try{
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(xpath));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
            element.click();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void printMessage(){
        try{
            WebElement aboutTxt = driver.findElement(By.xpath("//h1[contains(text(),'About')]"));
            System.out.println(aboutTxt.getText());
            WebElement printline1 = driver.findElement(By.xpath("(//p[contains(@class,'text-primary')])[1]"));
            System.out.println(printline1.getText());
            WebElement printline2 = driver.findElement(By.xpath("(//p[contains(@class,'text-primary')])[2]"));
            System.out.println(printline2.getText());
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void maturityLevel(){
        try{
            List<WebElement> matureElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.xpath("//*[@class='badges style-scope ytd-grid-movie-renderer']/div[2]/p")));
            if(!matureElements.isEmpty()){
                WebElement lastElement = matureElements.get(matureElements.size() - 1);
                String matureElementText = lastElement.getText().replaceAll("[^A-Za-z/\\d+]", "").trim();
                System.out.println(matureElementText);
                if(matureElementText.equals("A")){
                    System.out.println("This is an Adult movie. Please watch it if you are older than 18 years.");
                }
                else if(matureElementText.equals("U/A")){
                    System.out.println("Please watch this movie under Parental Guidance.");
                }
                else{
                    System.out.println("This is a Universal movie. Anybody may watch this movie.");
                }
                // Soft assert
                SoftAssert softAssert = new SoftAssert();
                softAssert.assertEquals(matureElementText, "A", "The movie is not marked as 'A' for Mature.");
                softAssert.assertEquals(matureElementText, "U", "The movie is not marked as 'U'.");
            }
        }
        catch(Exception e){
            System.out.println("Exception occurred: " + e.getMessage());
            // Log or handle the exception as needed
        }
    }

    public void genreOfLastMovie(){
        try{
            List<WebElement> movieGenreElement = driver
                    .findElements(
                            By.xpath("//*[@class='yt-simple-endpoint style-scope ytd-grid-movie-renderer']/span"));
            WebElement lastElement = movieGenreElement.get(movieGenreElement.size() - 1);
            String[] movieGenre = lastElement.getText().trim().split(" • ");
            String genreText = movieGenre[0];
            System.out.println(genreText);
            SoftAssert softAssert = new SoftAssert();
            softAssert.assertTrue(genreText.startsWith("Comedy") || genreText.startsWith("Animation"),
                    "Expected genre to be Comedy or Animation but found " + genreText);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void clickOnTab(String string){
        try{
            WebElement tabElement = wait.until(ExpectedConditions
                    .visibilityOfElementLocated(By.xpath("//a[@title='" + string + "']")));
            tabElement.click();
            System.out.println(string + " is clicked successfully.");
        }
        catch(Exception e){
            System.out.println(string + " Non-clickable element.");
            System.out.println(e.getMessage());
        }
    }

    public void gotoSection(int number){
        try{
            List<WebElement> sectionElements = wait.until(
                    ExpectedConditions.visibilityOfAllElementsLocatedBy(
                            By.xpath("//*[@id='dismissible' and @class='style-scope ytd-shelf-renderer']")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true)", number);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public String sectionName(int number) {
        List<WebElement> sectionElements = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(
                        By.xpath("(//*[@id='dismissible' and @class='style-scope ytd-shelf-renderer'])[" + number
                                + "]//span")));
        WebElement section = sectionElements.get(0);
        return section.getText();
    }

    public void scrollToRight(String sectionName) {
        try {
            WebElement sectionElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[@id='title' and contains(text(),'" + sectionName + "')]")));

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true)", sectionElement);

            WebElement nextButtonElement = wait
                    .until(ExpectedConditions
                            .visibilityOfElementLocated(By.xpath("//span[@id='title' and contains(text(),'"
                                    + sectionName
                                    + "')]//ancestor::div[@id='dismissible']//child::button[@aria-label='Next']")));

            while (nextButtonElement.isDisplayed()) {
                nextButtonElement.click();
                System.out.println("Click over next is successful.");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void nameOfLastPlayList(String sectionName) {
        List<WebElement> playListNames = driver.findElements(
                By.xpath("//a[contains(@title,'" + sectionName + "')]//ancestor::div[@id='dismissible']//child::h3"));
        WebElement lastPlayListElement = playListNames.get(playListNames.size() - 1);
        String playListHeadLineText = lastPlayListElement.getText().trim();
        System.out.println(playListHeadLineText);
    }

    public void noOfTracks(String sectionName, String playListName)
            throws InterruptedException {
        try {
            WebElement tracksElement = driver.findElement(By.xpath("//a[contains(@title,'" + sectionName
                    + "')]//ancestor::div[@id='dismissible']//h3[contains(text(),'" + playListName + "')]/../p"));
            String tracks = tracksElement.getText();
            String[] trackArray = tracks.split(" ");
            System.out.println(trackArray[0]);
            int trackCount = Integer.parseInt(trackArray[0]);
            SoftAssert softAssert = new SoftAssert();
            softAssert.assertEquals(trackCount <= 50, "The tracks listed are greater than 50: " + trackCount);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        Thread.sleep(1000);
    }

    public void titleOfNews() {
        try {
            WebElement latestNewsElement = wait.until(ExpectedConditions
                    .visibilityOfElementLocated(By.xpath("//span[contains(text(),'Latest news posts')]")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true)", latestNewsElement);
            List<WebElement> titleElements = driver.findElements(
                    By.xpath("//*[@class='style-scope ytd-post-renderer' and @id='header']/div[@id='author']/a/span"));
            List<WebElement> bodyElements = driver.findElements(
                    By.xpath("//*[@class='style-scope ytd-post-renderer' and @id='body']//*[@id='home-content-text']"));
            for (int i = 0; i < 3 && i < titleElements.size() && i < bodyElements.size(); i++) {
                System.out.println(titleElements.get(i).getText().trim());
                System.out.println(bodyElements.get(i).getText().trim());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void sumOfTheLikes() {
        int sum = 0;
        List<WebElement> likesElements = driver.findElements(By.xpath("//span[@id='vote-count-middle']"));
        for (int i = 0; i < 3 && i < likesElements.size(); i++) {
            String likes = likesElements.get(i).getText().trim();
            try {
                if (!likes.isEmpty()) {
                    sum += convertToNumericValue(likes);
                } else {
                    System.out.println("Count is null. " + i);
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Error parsing likes text: " + likes + " - " + e.getMessage());
            }
        }
        System.out.println("Total likes sum is: " + sum);
    }

    public static long convertToNumericValue(String value) {
        value = value.trim().toUpperCase();

        if (value.isEmpty()) {
            throw new IllegalArgumentException("Invalid format: " + value);
        }

        char lastChar = value.charAt(value.length() - 1);
        int multiplier = 1;
        switch (lastChar) {
            case 'K':
                multiplier = 1000;
                break;
            case 'M':
                multiplier = 1000000;
                break;
            case 'B':
                multiplier = 1000000000;
                break;
            default:
                if (Character.isDigit(lastChar)) {
                    return Long.parseLong(value);
                }
                throw new IllegalArgumentException("Invalid format: " + value);
        }

        String numericPart = value.substring(0, value.length() - 1);
        if (numericPart.isEmpty()) {
            throw new IllegalArgumentException("Invalid format: " + value);
        }
        double number = Double.parseDouble(numericPart);

        return (long) (number * multiplier);
    }

    public void click(By locator) {
        try {
            WebElement element = driver.findElement(locator);
            element.click();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void sendKeys(By locator, String text) {
        try {
            WebElement element = driver.findElement(locator);
            element.clear();
            element.sendKeys(text);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}