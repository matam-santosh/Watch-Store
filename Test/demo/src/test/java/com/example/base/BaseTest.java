package com.example.base;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BaseTest {

    protected WebDriver driver;
    protected WebDriverWait wait;

    protected static final String BASE_URL = "http://127.0.0.1:5500/";
    protected static final String LOGIN_URL = BASE_URL + "index.html";
    protected static final String HOME_URL = BASE_URL + "home.html";

    @BeforeClass
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(8));
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected void openLoginPage() {
        driver.get(LOGIN_URL);
        wait.until(ExpectedConditions.titleIs("Watch Store — Login"));
    }

    protected void clearBrowserState() {
        driver.manage().deleteAllCookies();
        clearWebStorageIfAvailable();
    }

    protected void clearWebStorageIfAvailable() {
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "try {"
                            + "  const p = window.location && window.location.protocol;"
                            + "  if (p === 'data:' || p === 'about:') return;"
                            + "  if (window.localStorage) window.localStorage.clear();"
                            + "  if (window.sessionStorage) window.sessionStorage.clear();"
                            + "} catch (e) { /* ignore */ }"
            );
        } catch (WebDriverException ignored) {
        }
    }

    protected void seedUser(String email, String password) {
        // App stores users in localStorage key "ws_users" as:
        // { "<email>": { "pwHash": "<sha256 hex>", "createdAt": "<iso>" } }
        String pwHash = sha256Hex(password);
        String createdAt = Instant.now().toString();
        String json = "{\"" + escapeJson(email) + "\":{\"pwHash\":\"" + pwHash + "\",\"createdAt\":\"" + createdAt + "\"}}";

        try {
            ((JavascriptExecutor) driver).executeScript(
                    "localStorage.setItem(arguments[0], arguments[1]);",
                    "ws_users",
                    json
            );
        } catch (WebDriverException ignored) {
        }
    }

    protected void waitForHomePage() {
        wait.until(ExpectedConditions.or(
                ExpectedConditions.titleIs("Watch Store — Home"),
                ExpectedConditions.urlContains("home.html")
        ));
    }

    protected void waitForLoginPage() {
        wait.until(ExpectedConditions.or(
                ExpectedConditions.titleIs("Watch Store — Login"),
                ExpectedConditions.urlContains("index.html")
        ));
    }

    private static String sha256Hex(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(String.valueOf(text).getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String escapeJson(String s) {
        return String.valueOf(s)
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}
