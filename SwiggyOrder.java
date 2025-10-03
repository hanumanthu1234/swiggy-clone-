package com.swiggy.automation;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.logging.*;
import java.io.File;


public class SwiggyOrder {
    static WebDriver driver;
    static WebDriverWait wait;
    static Logger logger = Logger.getLogger(SwiggyOrder.class.getName());

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "C:\\SeleniumDrivers\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        try {
            login();
            enterLocation("Bhimavaram");
            searchRestaurant("Ajantha Canteen");
            addFoodItem();
            viewAndUpdateCart();
            addAddress();
            proceedToPayment();
            logger.info("\u2705 Automation Complete. All steps executed successfully.");
        } catch (Exception e) {
            logger.severe("\u274C Fatal error: " + e.getMessage());
        } finally {
            // driver.quit();
        }
    }

    static void login() {
        driver.get("https://www.swiggy.com/");
        logger.info("Page Title: " + driver.getTitle());
        logger.info("Current URL: " + driver.getCurrentUrl());
        takeScreenshot("0_homepage_loaded.png");

        if (driver.getPageSource().contains("Logout") || driver.getPageSource().contains("My Account")) {
            logger.info("\u2705 Already logged in. Skipping login.");
            return;
        }

        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'Sign in')]"))).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("mobile"))).sendKeys("9542636841");
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'Login')]"))).click();

        logger.info("\u23F3 Waiting for OTP entry...");
        try {
            Thread.sleep(25000);
            WebElement otpInput = driver.switchTo().activeElement();
            otpInput.sendKeys(Keys.TAB);
            ((JavascriptExecutor) driver).executeScript("document.activeElement.blur();");
            logger.info("\uD83D\uDD04 Triggered OTP submit manually after input.");
        } catch (Exception e) {
            logger.warning("\u26A0\uFE0F Couldn't trigger OTP verification: " + e.getMessage());
        }
    }

    static void enterLocation(String location) throws InterruptedException {
        WebElement locationInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("location")));
        locationInput.sendKeys(location);
        Thread.sleep(2000);
        locationInput.sendKeys(Keys.DOWN, Keys.RETURN);
        Thread.sleep(5000);
        takeScreenshot("1_location_entered.png");
    }

    static void searchRestaurant(String name) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(@class,'sc-aXZVg') and text()='Search for restaurant, item or more']"))).click();
            Thread.sleep(1000);

            WebElement searchBar = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@placeholder='Search for restaurants and food']")));
            searchBar.clear();
            searchBar.sendKeys(name);
            searchBar.sendKeys(Keys.RETURN);
            logger.info("\uD83D\uDD0D Searching for: " + name);

            WebElement restaurantCard = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@data-testid='resturant-card-name' and text()='Ajantha Canteen']/ancestor::div[@aria-hidden='true']")));
            restaurantCard.click();
            logger.info("\u2705 Restaurant card clicked");
            Thread.sleep(3000);
            takeScreenshot("2_restaurant_card_clicked.png");

            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(text(),'ADD')]")));
            logger.info("\uD83C\uDF7B Menu loaded successfully.");
            takeScreenshot("3_menu_loaded.png");

        } catch (Exception e) {
            logger.severe("\u274C Error in searchRestaurant(): " + e.getMessage());
            takeScreenshot("search_restaurant_error.png");
        }
    }

    static void addFoodItem() {
        try {
            WebElement addBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@class,'add-button-center-container')]//div[text()='Add']")));
            addBtn.click();
            logger.info("\uD83D\uDED2 Added item to cart");
            Thread.sleep(2000);
            takeScreenshot("4_item_added.png");

            WebElement plusBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@class,'add-button-right-container')]//div[text()='+']")));
            plusBtn.click();
            logger.info("➕ Increased item quantity");
            Thread.sleep(2000);
            takeScreenshot("5_quantity_increased.png");

        } catch (Exception e) {
            logger.severe("\u274C Error in addFoodItem(): " + e.getMessage());
            takeScreenshot("add_food_item_error.png");
        }
    }

    static void viewAndUpdateCart() {
        try {
            WebElement viewCart = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(@class,'_1JiK6')]//span[text()='View Cart']")));
            viewCart.click();
            logger.info("\uD83D\uDED2 View Cart clicked");
            Thread.sleep(3000);
            takeScreenshot("6_view_cart.png");
        } catch (Exception e) {
            logger.severe("\u274C Error in viewAndUpdateCart(): " + e.getMessage());
        }
    }

    static void addAddress() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(text(),'Add New')]"))).click();
            logger.info("\uD83D\uDCCD Add New address clicked");
            Thread.sleep(2000);

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("building"))).sendKeys("12-34");
            driver.findElement(By.id("landmark")).sendKeys("Mavulamma Temple Road");

            try {
                WebElement areaInput = driver.findElement(By.id("area"));
                areaInput.sendKeys("Tattavarthy Street");
                logger.info("\uD83C\uDFE1 Area 'Tattavarthy Street' entered");
            } catch (NoSuchElementException e) {
                logger.info("\u2139\uFE0F Area input not present. Skipping.");
            }

            WebElement saveBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@class,'_1kz4H')]")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", saveBtn);
            logger.info("\uD83D\uDCEC Address saved and proceeding");
            Thread.sleep(4000);
            takeScreenshot("7_address_saved.png");

        } catch (Exception e) {
            logger.severe("\u274C Error in addAddress(): " + e.getMessage());
            takeScreenshot("address_error.png");
        }
    }

    static void proceedToPayment() {
        try {
            WebElement payBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@class,'_4dnMB') and text()='Proceed to Pay']")));
            payBtn.click();
            logger.info("\uD83D\uDCB3 Proceeded to payment");
            Thread.sleep(5000);
            takeScreenshot("8_payment_page.png");
        } catch (Exception e) {
            logger.severe("\u274C Error in proceedToPayment(): " + e.getMessage());
            takeScreenshot("payment_error.png");
        }
    }

    static void takeScreenshot(String filename) {
        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File src = ts.getScreenshotAs(OutputType.FILE);
            java.nio.file.Files.copy(src.toPath(), new java.io.File("screenshots/" + filename).toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            logger.warning("\u26A0\uFE0F Screenshot capture failed: " + e.getMessage());
        }
    }
}
