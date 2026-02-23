package com.example.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.example.base.BaseTest;
import com.example.page.LoginPage;

public class LoginTest extends BaseTest {

    LoginPage login;

  @BeforeMethod
  public void init() {
    clearBrowserState();
    openLoginPage();
    seedUser("user@test.com", "123456");
    openLoginPage();

    login = new LoginPage(driver);
    login.waitForLoaded();
}


    @Test
    public void verifyLoginPageTitle() {
        Assert.assertEquals(login.getPageTitle(), "Watch Store — Login");
    }

    @Test
    public void loginButtonIsEnabled() {
        Assert.assertTrue(login.isLoginButtonEnabled());
    }

    @Test
    public void emptyFields_areRejectedByHtmlValidation() {
        Assert.assertFalse(login.isLoginFormValid());
    }

    @Test
    public void emptyEmail_isRejectedByHtmlValidation() {
        login.enterPassword("123456");
        Assert.assertFalse(login.isLoginFormValid());
    }

    @Test
    public void emptyPassword_isRejectedByHtmlValidation() {
        login.enterEmail("test@gmail.com");
        Assert.assertFalse(login.isLoginFormValid());
    }

    @Test
    public void wrongPassword_showsErrorMessage() {
        login.enterEmail("user@test.com");
        login.enterPassword("123");
        login.clickLogin();
        Assert.assertFalse(login.getErrorMessage().isEmpty());
    }

    @Test
    public void invalidEmailFormat_isRejectedByHtmlValidation() {
        login.enterEmail("abc");
        login.enterPassword("123456");
        Assert.assertFalse(login.isLoginFormValid());
    }

    @Test
    public void validLogin_setsSession() {
        login.enterEmail("user@test.com");
        login.enterPassword("123456");
        login.clickLogin();
        Assert.assertEquals(login.waitForSessionEmail(), "user@test.com");
    }

    @Test
    public void sessionIsNotSetBeforeLogin() {
        Assert.assertNull(login.getLocalStorageUser());
    }
}
