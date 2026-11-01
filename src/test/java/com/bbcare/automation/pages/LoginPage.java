package com.bbcare.automation.pages;

import com.bbcare.automation.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * [MODULE M1: ĐĂNG NHẬP]
 * Page Object hoàn chỉnh cho màn hình /login.
 */
public class LoginPage extends BasePage {

    private final By usernameInput = By.cssSelector("[data-testid='login-identifier']");
    private final By passwordInput = By.cssSelector("[data-testid='login-password']");
    private final By loginButton   = By.cssSelector("[data-testid='login-submit-btn']");
    private final By alertMessage  = By.cssSelector("[data-testid='auth-alert-message']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open(String baseUrl) {
        driver.get(baseUrl + "/login");
        waitVisible(usernameInput);
        return this;
    }

    public void login(String username, String password) {
        type(usernameInput, username);
        type(passwordInput, password);
        click(loginButton);
    }

    public String getErrorMessage() {
        return getText(alertMessage);
    }

    public boolean isErrorDisplayed() {
        return isDisplayed(alertMessage);
    }

    public String getUsernameValidationMessage() {
        return waitVisible(usernameInput).getAttribute("validationMessage");
    }

    public String getPasswordValidationMessage() {
        return waitVisible(passwordInput).getAttribute("validationMessage");
    }
}
