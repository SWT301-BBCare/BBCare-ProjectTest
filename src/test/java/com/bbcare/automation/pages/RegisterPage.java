package com.bbcare.automation.pages;

import com.bbcare.automation.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * [MODULE M2: ĐĂNG KÝ TÀI KHOẢN]
 * Page Object hoàn chỉnh cho tab Đăng ký mới trên trang /login.
 */
public class RegisterPage extends BasePage {

    private final By registerTabBtn = By.cssSelector("[data-testid='tab-register']");
    private final By emailInput     = By.cssSelector("[data-testid='reg-email']");
    private final By passwordInput  = By.cssSelector("[data-testid='reg-password']");
    private final By nameInput      = By.cssSelector("[data-testid='reg-name']");
    private final By phoneInput     = By.cssSelector("[data-testid='reg-phone']");
    private final By addressInput   = By.cssSelector("[data-testid='reg-address']");
    private final By submitBtn      = By.cssSelector("[data-testid='reg-submit-btn']");
    private final By alertMessage   = By.cssSelector("[data-testid='auth-alert-message']");
    private final By successAlert   = By.cssSelector("[data-testid='auth-success-alert']");
    private final By errorAlert     = By.cssSelector("[data-testid='auth-error-alert']");

    public RegisterPage(WebDriver driver) {
        super(driver);
    }

    public RegisterPage open(String baseUrl) {
        driver.get(baseUrl + "/login");
        click(registerTabBtn);
        waitVisible(emailInput);
        return this;
    }

    public void register(String email, String password, String fullName, String phone, String address) {
        type(emailInput, email);
        type(passwordInput, password);
        type(nameInput, fullName);
        type(phoneInput, phone);
        type(addressInput, address);
        click(submitBtn);
    }

    public boolean isAlertDisplayed() {
        return isDisplayed(alertMessage);
    }

    public boolean isSuccessAlertDisplayed() {
        return isDisplayed(successAlert);
    }

    public boolean isErrorAlertDisplayed() {
        return isDisplayed(errorAlert);
    }

    public String getAlertMessage() {
        return getText(alertMessage);
    }
}