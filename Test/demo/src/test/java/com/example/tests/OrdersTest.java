package com.example.tests;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.example.base.BaseTest;
import com.example.page.LoginPage;
import com.example.page.OrdersPage;

public class OrdersTest extends BaseTest {

    private OrdersPage orders;
    private static final String TEST_EMAIL = "test@test.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String SEEDED_ORDER_ID = "ORD-TEST-1001";

    @BeforeMethod
    public void setupOrders() {
        ensureDriverSession();

        driver.get(BASE_URL + "index.html");
        clearBrowserState();
        driver.get(BASE_URL + "index.html");
        seedUser(TEST_EMAIL, TEST_PASSWORD);

        LoginPage login = new LoginPage(driver);
        login.waitForLoaded();
        login.enterEmail(TEST_EMAIL);
        login.enterPassword(TEST_PASSWORD);
        login.clickLogin();
        waitForHomePage();

        seedOrderData(TEST_EMAIL);

        driver.get(BASE_URL + "orders.html");
        orders = new OrdersPage(driver);
        orders.waitForLoaded();
    }

    @Test
    public void verifyOrdersPageTitle() {
        Assert.assertTrue(orders.getPageTitle().contains("Orders"));
    }

    @Test
    public void verifyHeroVisible() {
        Assert.assertTrue(orders.isHeroVisible());
    }

    @Test
    public void verifyOrdersWrapVisible() {
        Assert.assertTrue(orders.isOrdersWrapVisible());
    }

    @Test
    public void verifyOrderCardRendered() {
        Assert.assertTrue(orders.hasOrderCards());
    }

    @Test
    public void verifySeededOrderIdVisible() {
        Assert.assertTrue(orders.getOrdersText().contains(SEEDED_ORDER_ID));
    }

    @Test
    public void verifySeededOrderItemVisible() {
        Assert.assertTrue(orders.getOrdersText().contains("Neo Chrono X1"));
    }

    @Test
    public void verifySeededOrderTotalVisible() {
        Assert.assertTrue(orders.getOrdersText().contains("7,999") || orders.getOrdersText().contains("7999"));
    }

    @Test
    public void verifySeededOrderQuantityVisible() {
        Assert.assertTrue(orders.getOrdersText().contains("1×") || orders.getOrdersText().contains("1x"));
    }

    @Test
    public void verifyCartCountVisibleInHeader() {
        Assert.assertNotNull(orders.getCartCount());
    }

    @Test
    public void verifyOrdersPersistAfterRefresh() {
        driver.navigate().refresh();
        orders.waitForLoaded();
        Assert.assertTrue(orders.getOrdersText().contains(SEEDED_ORDER_ID));
    }

    @Test
    public void verifyOrdersPageLoadsFast() {
        long start = System.currentTimeMillis();
        orders.isOrdersWrapVisible();
        long end = System.currentTimeMillis();
        Assert.assertTrue((end - start) < 3000);
    }

    @Test
    public void verifyOpenHomeAndBackToOrders() {
        driver.get(BASE_URL + "home.html");
        waitForHomePage();
        driver.get(BASE_URL + "orders.html");
        orders.waitForLoaded();
        Assert.assertTrue(orders.getPageTitle().contains("Orders"));
    }

    @Test
    public void verifyLogoutFromOrders() {
        orders.logout();
        driver.get(BASE_URL + "home.html");
        waitForLoginPage();
        Assert.assertTrue(driver.getTitle().contains("Login"));
    }

    @Test
    public void verifyOrdersTextNotBlank() {
        Assert.assertFalse(orders.getOrdersText().isBlank());
    }

    @Test
    public void verifyOrdersHasPurchaseHistoryLabel() {
        Assert.assertTrue(orders.getOrdersText().toLowerCase().contains("order"));
    }

    private void seedOrderData(String email) {
        ((JavascriptExecutor) driver).executeScript(
                "try {"
                        + "const email = arguments[0];"
                        + "const orderId = arguments[1];"
                        + "const key = `ws_orders:${email}`;"
                        + "const order = {"
                        + "  id: orderId,"
                        + "  createdAt: new Date().toISOString(),"
                        + "  total: 7999,"
                        + "  items: [{id:'neo-chrono', title:'Neo Chrono X1', price:7999, qty:1, total:7999}]"
                        + "};"
                        + "localStorage.setItem(key, JSON.stringify([order]));"
                        + "localStorage.setItem(`ws_lastOrder:${email}`, orderId);"
                        + "} catch(e) {}",
                email,
                SEEDED_ORDER_ID
        );
    }

    private void ensureDriverSession() {
        try {
            if (driver == null) {
                driver = new ChromeDriver();
                driver.manage().window().maximize();
                wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(8));
                return;
            }
            driver.getTitle();
            if (driver.getWindowHandles().isEmpty()) {
                throw new NoSuchWindowException("No active browser window");
            }
        } catch (WebDriverException ex) {
            try {
                driver.quit();
            } catch (Exception ignored) {
            }
            driver = new ChromeDriver();
            driver.manage().window().maximize();
            wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(8));
        }
    }
}
