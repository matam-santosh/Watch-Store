package com.example.page;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage {

    WebDriver driver;
    WebDriverWait wait;

    // ✅ CORRECT locators (matching your HTML)
    private By loginEmail = By.id("loginEmail");
    private By loginPassword = By.id("loginPassword");
    private By loginBtn = By.id("loginBtn");
    private By errorMsg = By.id("loginErr");
    private By loginForm = By.id("loginForm");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    // Actions
    public void open() {
        driver.get("http://127.0.0.1:5500/index.html");
    }

    public void enterEmail(String value) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(loginEmail)).clear();
        driver.findElement(loginEmail).sendKeys(value);
    }

    public void enterPassword(String value) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(loginPassword)).clear();
        driver.findElement(loginPassword).sendKeys(value);
    }

    public void clickLogin() {
        wait.until(ExpectedConditions.elementToBeClickable(loginBtn)).click();
    }

    public String getErrorMessage() {
        wait.until(ExpectedConditions.presenceOfElementLocated(errorMsg));
        return wait.until(d -> {
            String text = d.findElement(errorMsg).getText();
            return (text != null && !text.trim().isEmpty()) ? text : null;
        });
    }

    public boolean isLoginButtonEnabled() {
        return driver.findElement(loginBtn).isEnabled();
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    public boolean isLoginFormValid() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Object ok = js.executeScript("return document.querySelector('#loginForm')?.checkValidity() ?? false;");
        return Boolean.TRUE.equals(ok);
    }

    public String getLocalStorageUser() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        return (String) js.executeScript(
                "try {"
                        + "  const s = JSON.parse(localStorage.getItem('ws_session') || 'null');"
                        + "  return (s && s.email) ? s.email : null;"
                        + "} catch (e) { return null; }"
        );
    }

    public String waitForSessionEmail() {
        return wait.until(d -> {
            JavascriptExecutor js = (JavascriptExecutor) d;
            String email = (String) js.executeScript(
                    "try {"
                            + "  const s = JSON.parse(localStorage.getItem('ws_session') || 'null');"
                            + "  return (s && s.email) ? s.email : null;"
                            + "} catch (e) { return null; }"
            );
            return (email != null && !email.trim().isEmpty()) ? email : null;
        });
    }

    public void waitForLoaded() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(loginForm));
        wait.until(ExpectedConditions.visibilityOfElementLocated(loginEmail));
        wait.until(ExpectedConditions.visibilityOfElementLocated(loginPassword));
    }
}
