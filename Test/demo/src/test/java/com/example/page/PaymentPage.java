package com.example.page;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PaymentPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By heroTitle = By.tagName("h1");
    private final By payTotal = By.id("payTotal");
    private final By orderSummary = By.id("orderSummary");
    private final By paymentForm = By.id("paymentForm");
    private final By cardName = By.id("cardName");
    private final By cardNumber = By.id("cardNumber");
    private final By expiry = By.id("expiry");
    private final By cvv = By.id("cvv");
    private final By zip = By.id("zip");
    private final By payErr = By.id("payErr");
    private final By payBtn = By.id("payBtn");
    private final By backToCart = By.cssSelector("a.btn.ghost[href='./cart.html']");
    private final By cartCount = By.cssSelector("[data-cart-count]");
    private final By logoutBtn = By.id("logoutBtn");

    public PaymentPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(6));
    }

    public void waitForLoaded() {
        wait.until(d -> {
            String url = d.getCurrentUrl();
            Object page = ((JavascriptExecutor) d).executeScript(
                    "return document.body ? document.body.getAttribute('data-page') : null;");
            return (url != null && url.contains("payment.html")) || "payment".equals(String.valueOf(page));
        });
        wait.until(ExpectedConditions.visibilityOfElementLocated(paymentForm));
        wait.until(ExpectedConditions.visibilityOfElementLocated(payBtn));
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    public boolean isHeroVisible() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(heroTitle)).isDisplayed();
    }

    public boolean isPaymentFormVisible() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(paymentForm)).isDisplayed();
    }

    public boolean isOrderSummaryVisible() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(orderSummary)).isDisplayed();
    }

    public String getPayTotal() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(payTotal)).getText();
    }

    public boolean isPayButtonEnabled() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(payBtn)).isEnabled();
    }

    public String getCartCount() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(cartCount)).getText();
    }

    public void enterCardName(String value) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(cardName)).clear();
        driver.findElement(cardName).sendKeys(value);
    }

    public void enterCardNumber(String value) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(cardNumber)).clear();
        driver.findElement(cardNumber).sendKeys(value);
    }

    public void enterExpiry(String value) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(expiry)).clear();
        driver.findElement(expiry).sendKeys(value);
    }

    public void enterCvv(String value) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(cvv)).clear();
        driver.findElement(cvv).sendKeys(value);
    }

    public void enterZip(String value) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(zip)).clear();
        driver.findElement(zip).sendKeys(value);
    }

    public void submitPayment() {
        wait.until(ExpectedConditions.elementToBeClickable(payBtn)).click();
    }

    public void clickBackToCart() {
        wait.until(ExpectedConditions.elementToBeClickable(backToCart)).click();
    }

    public void logout() {
        wait.until(ExpectedConditions.elementToBeClickable(logoutBtn)).click();
    }

    public String getErrorMessage() {
        wait.until(ExpectedConditions.presenceOfElementLocated(payErr));
        String text = driver.findElement(payErr).getText();
        return text == null ? "" : text;
    }

    public boolean isPaymentFormValid() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Object valid = js.executeScript("return document.querySelector('#paymentForm')?.checkValidity() ?? false;");
        return Boolean.TRUE.equals(valid);
    }
}
