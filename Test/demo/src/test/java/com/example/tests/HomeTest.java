package com.example.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.openqa.selenium.TimeoutException;

import com.example.base.BaseTest;
import com.example.page.HomePage;
import com.example.page.LoginPage;

public class HomeTest extends BaseTest {

    HomePage home;

    @BeforeMethod
    public void loginToHome() {
        TimeoutException last = null;

        for (int attempt = 0; attempt < 2; attempt++) {
            try {
                clearBrowserState();
                openLoginPage();
                seedUser("user@test.com", "123456");

                LoginPage login = new LoginPage(driver);
                login.waitForLoaded();
                login.enterEmail("user@test.com");
                login.enterPassword("123456");
                login.clickLogin();
                waitForHomePage();
                last = null;
                break;
            } catch (TimeoutException ex) {
                last = ex;
                driver.get(LOGIN_URL);
            }
        }

        if (last != null) {
            throw last;
        }

        home = new HomePage(driver);
        home.waitForLoaded();
    }

    @Test
    public void verifyHomePageTitle() {
        Assert.assertEquals(home.getPageTitle(), "Watch Store — Home");
    }

    @Test
    public void verifyHeroSectionIsVisible() {
        Assert.assertTrue(home.isHeroTitleDisplayed());
    }

    @Test
    public void verifySearchBoxIsVisible() {
        Assert.assertTrue(home.isSearchBoxVisible());
    }

    @Test
    public void verifyProductsGridIsDisplayed() {
        Assert.assertTrue(home.isProductsGridVisible());
    }

    @Test
    public void verifySearchResultsCountUpdates() {
        String before = home.getResultsCount();
        home.searchProduct("neo");
        String after = home.getResultsCount();
        Assert.assertNotNull(after);
        Assert.assertFalse(after.isBlank());
        Assert.assertNotEquals(after, before);
    }

    @Test
    public void verifyCartCountInitiallyZero() {
        Assert.assertEquals(home.getCartItemCount(), "0");
    }

    @Test
    public void verifyLogoutRedirectsToLoginPage() {
        home.logout();
        waitForLoginPage();
        Assert.assertEquals(driver.getTitle(), "Watch Store — Login");
    }

    @Test
    public void verifyInitialResultsCountMatchesCards() {
        int cards = home.getProductsCount();
        int count = parseResultsCount(home.getResultsCount());
        Assert.assertTrue(cards > 0, "Expected at least one product");
        Assert.assertEquals(count, cards, "resultsCount should match number of cards");
    }

    @Test
    public void verifySearchByNameReducesResults() {
        String beforeText = home.getResultsCount();
        int before = parseResultsCount(beforeText);

        home.searchProduct("Neo Chrono");
        home.waitForResultsCountToChange(beforeText);

        int after = parseResultsCount(home.getResultsCount());
        int cards = home.getProductsCount();

        Assert.assertTrue(after > 0, "Expected at least one result for Neo Chrono");
        Assert.assertTrue(after <= before, "Filtered results should not exceed original count");
        Assert.assertEquals(after, cards, "Text count and card count should match");
    }

    @Test
    public void verifySearchUnknownShowsZeroResults() {
        String beforeText = home.getResultsCount();
        home.searchProduct("this-does-not-exist-123");
        home.waitForResultsCountToChange(beforeText);

        int count = parseResultsCount(home.getResultsCount());
        int cards = home.getProductsCount();

        Assert.assertEquals(count, 0, "Unknown query should show zero results");
        Assert.assertEquals(cards, 0, "No product cards should be visible for unknown query");
    }

    @Test
    public void verifyResultsCountTextFormat() {
        String text = home.getResultsCount();
        Assert.assertTrue(text.matches("\\d+\\s+watch(es)?"), "resultsCount text should look like '12 watches'");
    }

    @Test
    public void verifyHomeNavPillIsActive() {
        Assert.assertTrue(home.isHomeNavActive(), "Home nav pill should be marked active");
    }

    @Test
    public void verifyToastIsHiddenByDefault() {
        Assert.assertFalse(home.isToastVisible(), "Toast should not be visible on initial load");
    }

    @Test
    public void verifyReloadKeepsUserOnHomePage() {
        driver.navigate().refresh();
        home.waitForLoaded();
        Assert.assertEquals(home.getPageTitle(), "Watch Store — Home");
    }

    @Test
    public void verifyHomeRedirectsToLoginWhenNotAuthenticated() {
        clearBrowserState();
        driver.get(HOME_URL);
        waitForLoginPage();
        Assert.assertEquals(driver.getTitle(), "Watch Store — Login");
    }

    private int parseResultsCount(String text) {
        String digits = text.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(digits);
    }
}
