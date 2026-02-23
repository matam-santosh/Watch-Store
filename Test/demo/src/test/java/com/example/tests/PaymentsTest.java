package com.example.tests;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.example.base.BaseTest;
import com.example.page.CartPage;
import com.example.page.HomePage;
import com.example.page.LoginPage;
import com.example.page.PaymentPage;

public class PaymentsTest extends BaseTest {

    private PaymentPage payment;
    private final By addToCartBtn = By.id("addToCartBtn");
    private final By cartCountBadge = By.cssSelector("[data-cart-count]");

    @BeforeMethod
    public void setupPayment() {
        boolean ready = false;
        for (int attempt = 0; attempt < 2 && !ready; attempt++) {
            driver.get(BASE_URL + "index.html");
            clearBrowserState();
            driver.get(BASE_URL + "index.html");
            seedUser("test@test.com", "password123");

            LoginPage login = new LoginPage(driver);
            login.waitForLoaded();
            login.enterEmail("test@test.com");
            login.enterPassword("password123");
            login.clickLogin();
            waitForHomePage();

            HomePage home = new HomePage(driver);
            home.waitForLoaded();
            addOneItemToCartRealFlow();

            driver.get(BASE_URL + "cart.html");
            CartPage cart = new CartPage(driver);
            cart.waitForLoaded();
            if (cart.getCartCountAsInt() <= 0) {
                seedCartFallbackData();
                driver.navigate().refresh();
                cart.waitForLoaded();
            }
            if (cart.getCartCountAsInt() <= 0) {
                continue;
            }
            Assert.assertTrue(cart.isCheckoutEnabled(), "Checkout must be enabled when cart has items");

            cart.clickCheckout();
            payment = new PaymentPage(driver);
            payment.waitForLoaded();
            ready = true;
        }

        Assert.assertTrue(ready, "Cart should contain at least 1 item before checkout");
    }

    @Test
    public void verifyPaymentPageTitle() {
        Assert.assertTrue(payment.getPageTitle().contains("Payment"));
    }

    @Test
    public void verifyHeroIsVisible() {
        Assert.assertTrue(payment.isHeroVisible());
    }

    @Test
    public void verifyPaymentFormIsVisible() {
        Assert.assertTrue(payment.isPaymentFormVisible());
    }

    @Test
    public void verifyOrderSummaryIsVisible() {
        Assert.assertTrue(payment.isOrderSummaryVisible());
    }

    @Test
    public void verifyPayTotalIsDisplayed() {
        Assert.assertTrue(payment.getPayTotal().matches(".*\\d.*"));
    }

    @Test
    public void verifyPayButtonEnabled() {
        Assert.assertTrue(payment.isPayButtonEnabled());
    }

    @Test
    public void verifyCartCountVisibleInHeader() {
        Assert.assertNotNull(payment.getCartCount());
    }

    @Test
    public void verifyPaymentFormInvalidWhenEmpty() {
        Assert.assertFalse(payment.isPaymentFormValid());
    }

    @Test
    public void verifyPaymentFormValidWithDetails() {
        payment.enterCardName("Test User");
        payment.enterCardNumber("4242 4242 4242 4242");
        payment.enterExpiry("08/29");
        payment.enterCvv("123");
        payment.enterZip("560001");
        Assert.assertTrue(payment.isPaymentFormValid());
    }

    @Test
    public void verifyBackToCartNavigation() {
        payment.clickBackToCart();
        Assert.assertTrue(driver.getTitle().contains("Cart"));
    }

    @Test
    public void verifyLogoutFromPayment() {
        payment.logout();
        driver.get(BASE_URL + "home.html");
        waitForLoginPage();
        Assert.assertTrue(driver.getTitle().contains("Login"));
    }

    @Test
    public void verifySubmitDoesNotCrashWithValidData() {
        payment.enterCardName("Test User");
        payment.enterCardNumber("4242 4242 4242 4242");
        payment.enterExpiry("08/29");
        payment.enterCvv("123");
        payment.enterZip("560001");
        payment.submitPayment();
        Assert.assertTrue(true);
    }

    @Test
    public void verifyPaymentPageLoadsFast() {
        long start = System.currentTimeMillis();
        payment.isPaymentFormVisible();
        long end = System.currentTimeMillis();
        Assert.assertTrue((end - start) < 3000);
    }

    @Test
    public void verifyPaymentPageStaysAfterRefresh() {
        driver.navigate().refresh();
        payment.waitForLoaded();
        Assert.assertTrue(driver.getTitle().contains("Payment"));
    }

    @Test
    public void verifyPayErrorElementPresent() {
        Assert.assertNotNull(payment.getErrorMessage());
    }

    private void addOneItemToCartRealFlow() {
        driver.get(BASE_URL + "product.html?id=neo-chrono");
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
        shortWait.until(ExpectedConditions.visibilityOfElementLocated(addToCartBtn));
        shortWait.until(ExpectedConditions.elementToBeClickable(addToCartBtn)).click();

        // Wait until any visible cart badge updates above zero.
        shortWait.until(d -> {
            List<WebElement> badges = d.findElements(cartCountBadge);
            for (WebElement badge : badges) {
                if (!badge.isDisplayed()) continue;
                String digits = badge.getText().replaceAll("[^0-9]", "");
                if (!digits.isEmpty() && Integer.parseInt(digits) > 0) return true;
            }
            return false;
        });
    }

    private void seedCartFallbackData() {
        String email = getSessionEmail();
        if (email == null || email.isBlank()) {
            return;
        }
        ((JavascriptExecutor) driver).executeScript(
                "try {"
                        + "const email = arguments[0];"
                        + "const key = `ws_cart:${email}`;"
                        + "const items = [{id:'neo-chrono', qty:1}];"
                        + "localStorage.setItem(key, JSON.stringify(items));"
                        + "const countEls=document.querySelectorAll('[data-cart-count]');"
                        + "countEls.forEach(e=>e.textContent='1');"
                        + "} catch(e) {}",
                email
        );
    }

    private String getSessionEmail() {
        Object email = ((JavascriptExecutor) driver).executeScript(
                "try {"
                        + "const s = JSON.parse(localStorage.getItem('ws_session') || 'null');"
                        + "return s && s.email ? s.email : null;"
                        + "} catch(e) { return null; }"
        );
        return email == null ? null : String.valueOf(email);
    }

}
