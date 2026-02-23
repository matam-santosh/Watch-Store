package com.example.page;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CartPage {

    WebDriver driver;
    WebDriverWait wait;

    // ===== Locators from cart.html =====
    private By cartTitle = By.tagName("h1");
    private By cartTable = By.id("cartTable");
    private By grandTotal = By.id("grandTotal");
    private By checkoutBtn = By.id("checkoutBtn");
    private By continueShoppingLink = By.linkText("Continue shopping");
    private By cartCount = By.cssSelector("[data-cart-count]");
    private By logoutBtn = By.id("logoutBtn");

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    // ===== Page actions / getters =====
    public String getPageTitle() {
        return driver.getTitle();
    }

    public void waitForLoaded() {
        wait.until(ExpectedConditions.urlContains("cart.html"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(cartTitle));
    }

    public boolean isCartTitleVisible() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(cartTitle)).isDisplayed();
    }

    public boolean isCartTableVisible() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(cartTable)).isDisplayed();
    }

    public String getGrandTotal() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(grandTotal)).getText();
    }

    public boolean isCheckoutEnabled() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(checkoutBtn)).isEnabled();
    }

    public void clickCheckout() {
        wait.until(ExpectedConditions.elementToBeClickable(checkoutBtn)).click();
    }

    public void clickContinueShopping() {
        wait.until(ExpectedConditions.elementToBeClickable(continueShoppingLink)).click();
    }

    public String getCartCount() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(cartCount)).getText();
    }

    public int getCartCountAsInt() {
        String digits = getCartCount().replaceAll("[^0-9]", "");
        if (digits.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(digits);
    }

    public void logout() {
        wait.until(ExpectedConditions.elementToBeClickable(logoutBtn)).click();
    }
}

