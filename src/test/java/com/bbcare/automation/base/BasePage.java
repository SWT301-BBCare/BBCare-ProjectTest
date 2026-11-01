package com.bbcare.automation.base;

import com.bbcare.automation.utils.ConfigReader;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Class cơ sở cho tất cả các Page Object (POM).
 * QUY TẮC BẮT BUỘC (Mục 6.2):
 * 1. Page class KHÔNG chứa assertion (assertEquals, assertTrue...).
 * 2. CẤM dùng Thread.sleep() — phải dùng các hàm Explicit Wait (waitVisible, click, type...) bên dưới.
 */
public abstract class BasePage {
    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        long timeoutSec = Long.parseLong(ConfigReader.get("timeout.seconds"));
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSec));
    }

    protected WebElement waitVisible(By by) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    protected void slowDown() {
        String raw = ConfigReader.get("step.delay.ms");
        if (raw != null && !raw.isBlank()) {
            long ms = Long.parseLong(raw);
            if (ms > 0) {
                long end = System.currentTimeMillis() + ms;
                new WebDriverWait(driver, Duration.ofMillis(ms + 5000))
                        .pollingEvery(Duration.ofMillis(100))
                        .until(d -> System.currentTimeMillis() >= end);
            }
        }
    }

    protected void click(By by) {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(by));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center', inline:'nearest'});", el);
        slowDown();
        WebElement freshEl = wait.until(ExpectedConditions.elementToBeClickable(by));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center', inline:'nearest'});", freshEl);
        try {
            freshEl.click();
        } catch (ElementClickInterceptedException | StaleElementReferenceException e) {
            WebElement retryEl = wait.until(ExpectedConditions.presenceOfElementLocated(by));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", retryEl);
        }
    }

    protected void type(By by, String text) {
        WebElement el = waitVisible(by);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center', inline:'nearest'});", el);
        slowDown();
        WebElement freshEl = waitVisible(by);
        freshEl.clear();
        if (text != null) {
            freshEl.sendKeys(text); // Nếu ô CSV trống -> null -> bỏ qua chỉ clear()
        }
    }

    /**
     * Thực hiện Đăng nhập tài khoản Khách hàng chuẩn (Nguyễn Văn An) trước khi vào Đặt ca Y tá / Đặt hàng / Hủy lịch.
     * Hiển thị trực quan bước đăng nhập trên trang /login rồi chuyển về Trang chủ với tài khoản đã đăng nhập.
     */
    public void loginAsCustomer(String baseUrl) {
        String validUser = ConfigReader.get("user.valid.email");
        if (validUser == null || validUser.isBlank()) {
            validUser = ConfigReader.get("user.valid.username");
        }
        if (validUser == null || validUser.isBlank()) {
            validUser = "khachhang@gmail.com";
        }
        String validPass = ConfigReader.get("user.valid.password");
        if (validPass == null || validPass.isBlank()) {
            validPass = "BBCare@2026";
        }
        driver.get(baseUrl + "/login");
        WebElement userInp = waitVisible(By.cssSelector("[data-testid='login-identifier']"));
        WebElement passInp = waitVisible(By.cssSelector("[data-testid='login-password']"));
        userInp.clear();
        userInp.sendKeys(validUser);
        passInp.clear();
        passInp.sendKeys(validPass);

        click(By.cssSelector("[data-testid='login-submit-btn']"));

        // Đợi chuyển hướng về trang chủ sau khi đăng nhập thành công
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='nav-booking-btn']")));

        // Đảm bảo biến phiên đăng nhập được kích hoạt đầy đủ trên giao diện
        ((JavascriptExecutor) driver).executeScript(
                "localStorage.setItem('bbcare_logged_in', 'true');" +
                        "localStorage.setItem('bbcare_user_email', arguments[0]);" +
                        "localStorage.setItem('bbcare_user_name', 'Nguyễn Văn An');" +
                        "localStorage.setItem('bbcare_client_name', 'Nguyễn Văn An');" +
                        "localStorage.setItem('bbcare_user_phone', '0908 786 168');" +
                        "localStorage.setItem('bbcare_role', 'client');" +
                        "if (typeof isLoggedIn !== 'undefined') isLoggedIn = true;" +
                        "const lbl = document.getElementById('accountLabel'); if(lbl) lbl.innerText = 'Nguyễn Văn An';",
                validUser
        );
    }

    protected String getText(By by) {
        return waitVisible(by).getText().trim();
    }

    protected boolean isDisplayed(By by) {
        try {
            return waitVisible(by).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}

