package com.bbcare.automation.tests;

import com.bbcare.automation.base.BaseTest;
import com.bbcare.automation.pages.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * [THÀNH VIÊN 1 PHỤ TRÁCH - MODULE M1: ĐĂNG NHẬP]
 * Chạy thử lệnh: mvn test -Dtest=LoginTest
 */
@DisplayName("M1 - Kiểm thử chức năng Đăng nhập")
class LoginTest extends BaseTest {

    @ParameterizedTest(name = "{0}: user={1} -> expected={3}")
    @CsvFileSource(resources = "/testdata/login_data.csv", numLinesToSkip = 1, encoding = "UTF-8")
    void testLoginDataDriven(String tcId, String username, String password,
                             String expectedResult, String expectedMessage) {
        LoginPage loginPage = new LoginPage(driver).open(BASE_URL);
        loginPage.login(username, password);

        switch (expectedResult) {
            case "SUCCESS" -> {
                new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(10))
                        .until(org.openqa.selenium.support.ui.ExpectedConditions.not(
                                org.openqa.selenium.support.ui.ExpectedConditions.urlContains("/login")));
                assertFalse(loginPage.getCurrentUrl().contains("/login"), tcId + " phải rời khỏi trang /login");
            }
            case "FAIL" -> {
                assertTrue(loginPage.isErrorDisplayed(), tcId + " phải hiển thị thông báo lỗi");
                assertEquals(expectedMessage, loginPage.getErrorMessage(), tcId);
            }
            case "REQUIRED" -> {
                assertTrue(loginPage.getCurrentUrl().contains("/login"), tcId + " phải giữ nguyên ở trang /login");
            }
            default -> fail("expectedResult không hợp lệ trong CSV: " + expectedResult);
        }
    }
}

