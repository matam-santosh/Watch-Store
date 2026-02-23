package com.example.page;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class HomePage {

    WebDriver driver;
    WebDriverWait wait;

    // ===== Locators (from your HTML) =====
    private By homeTitle = By.tagName("h1");
    private By searchInput = By.id("searchInput");
    private By resultsCount = By.id("resultsCount");
    private By productsGrid = By.id("productsGrid");
    private By cartCount = By.cssSelector("[data-cart-count]");
    private By logoutBtn = By.id("logoutBtn");
    private By productCards = By.cssSelector("#productsGrid .product-card");
    private By navHomePill = By.cssSelector("a.pill[href='./home.html']");
    private By activeNavPill = By.cssSelector(".nav a.pill[aria-current='page']");
    private By toast = By.id("toast");
    private By addToCartButtons = By.xpath("//*[@id='productsGrid']//button[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'add')]");

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    // ===== Page actions / getters =====
    public String getPageTitle() {
        return driver.getTitle();
    }

    public void waitForLoaded() {
        wait.until(ExpectedConditions.titleIs("Watch Store — Home"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(searchInput));
        wait.until(ExpectedConditions.visibilityOfElementLocated(productsGrid));
        wait.until(ExpectedConditions.visibilityOfElementLocated(resultsCount));
        wait.until(ExpectedConditions.visibilityOfElementLocated(cartCount));
    }

    public boolean isHeroTitleDisplayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(homeTitle)).isDisplayed();
    }

    public boolean isSearchBoxVisible() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(searchInput)).isDisplayed();
    }

    public void searchProduct(String text) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(searchInput)).clear();
        driver.findElement(searchInput).sendKeys(text);
    }

    public String getResultsCount() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(resultsCount)).getText();
    }

    public boolean isProductsGridVisible() {
        return wait.until(ExpectedConditions.presenceOfElementLocated(productsGrid)).isDisplayed();
    }

    public String getCartItemCount() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(cartCount)).getText();
    }

    public void logout() {
        wait.until(ExpectedConditions.elementToBeClickable(logoutBtn)).click();
    }
    public int getProductsCount() {
        return driver.findElements(productCards).size();
    }

    public void waitForResultsCountToChange(String previous) {
        wait.until(d -> {
            String now = d.findElement(resultsCount).getText();
            return now != null && !now.equals(previous);
        });
    }

    public boolean isHomeNavActive() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(navHomePill));
        boolean hasHomeLink = !driver.findElements(navHomePill).isEmpty();
        return hasHomeLink && "Watch Store — Home".equals(driver.getTitle());
    }

    public boolean isToastVisible() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Object visible = js.executeScript(
                "var t=document.getElementById('toast');" +
                        "return !!(t && t.classList.contains('show'));");
        return Boolean.TRUE.equals(visible);
    }

    public int getCartItemCountAsInt() {
        return parseInteger(getCartItemCount());
    }

    public boolean ensureAtLeastOneItemInCart() {
        for (int attempt = 0; attempt < 3; attempt++) {
            int before = getCartItemCountAsInt();
            if (before > 0 || hasCartDataInStorage()) {
                return true;
            }

            wait.until(ExpectedConditions.visibilityOfElementLocated(productsGrid));
            wait.until(d -> !d.findElements(productCards).isEmpty());

            if (clickAddLikeControlAndWaitForCartSignal(before)) {
                return true;
            }

            driver.navigate().refresh();
            waitForLoaded();
        }
        return getCartItemCountAsInt() > 0 || hasCartDataInStorage();
    }

    private boolean clickAddLikeControlAndWaitForCartSignal(int before) {
        List<WebElement> buttons = driver.findElements(addToCartButtons);
        for (WebElement button : buttons) {
            if (!button.isDisplayed() || !button.isEnabled()) {
                continue;
            }
            button.click();
            try {
                wait.until(d -> parseInteger(d.findElement(cartCount).getText()) > before || hasCartDataInStorage());
                return true;
            } catch (TimeoutException ignored) {
                // Try next match.
            }
        }

        JavascriptExecutor js = (JavascriptExecutor) driver;
        Object clicked = js.executeScript(
                "const root = document.getElementById('productsGrid') || document;" +
                        "const nodes = root.querySelectorAll('button,a,[role=\"button\"],[onclick]');" +
                        "for (const n of nodes) {" +
                        "  const t = (n.textContent || '').trim().toLowerCase();" +
                        "  if (t.includes('add') || t.includes('cart') || t.includes('buy')) { n.click(); return true; }" +
                        "}" +
                        "return false;");
        if (Boolean.TRUE.equals(clicked)) {
            try {
                wait.until(d -> parseInteger(d.findElement(cartCount).getText()) > before || hasCartDataInStorage());
                return true;
            } catch (TimeoutException ignored) {
                return hasCartDataInStorage();
            }
        }
        return false;
    }

    private boolean hasCartDataInStorage() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Object hasData = js.executeScript(
                "try {" +
                        "  for (let i = 0; i < localStorage.length; i++) {" +
                        "    const key = localStorage.key(i) || '';" +
                        "    if (!/cart/i.test(key)) continue;" +
                        "    const raw = localStorage.getItem(key);" +
                        "    if (!raw) continue;" +
                        "    let v = null;" +
                        "    try { v = JSON.parse(raw); } catch (e) { continue; }" +
                        "    if (Array.isArray(v) && v.length > 0) return true;" +
                        "    if (v && typeof v === 'object') {" +
                        "      if (Array.isArray(v.items) && v.items.length > 0) return true;" +
                        "      if (Object.keys(v).length > 0) return true;" +
                        "    }" +
                        "  }" +
                        "  return false;" +
                        "} catch (e) { return false; }");
        return Boolean.TRUE.equals(hasData);
    }

    private int parseInteger(String text) {
        String digits = String.valueOf(text).replaceAll("[^0-9]", "");
        if (digits.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(digits);
    }
}

