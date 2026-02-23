package com.example.page;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class OrdersPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By heroTitle = By.tagName("h1");
    private final By ordersWrap = By.id("ordersWrap");
    private final By cartCount = By.cssSelector("[data-cart-count]");
    private final By logoutBtn = By.id("logoutBtn");
    private final By orderCards = By.cssSelector("#ordersWrap .card");

    public OrdersPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(6));
    }

    public void waitForLoaded() {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(ordersWrap));
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    public boolean isHeroVisible() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(heroTitle)).isDisplayed();
    }

    public boolean isOrdersWrapVisible() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(ordersWrap)).isDisplayed();
    }

    public String getOrdersText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(ordersWrap)).getText();
    }

    public boolean hasOrderCards() {
        return !driver.findElements(orderCards).isEmpty();
    }

    public String getCartCount() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(cartCount)).getText();
    }

    public void logout() {
        wait.until(ExpectedConditions.elementToBeClickable(logoutBtn)).click();
    }
}
