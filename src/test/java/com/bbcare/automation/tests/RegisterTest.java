package com.bbcare.automation.tests;

import com.bbcare.automation.base.BaseTest;
import com.bbcare.automation.pages.RegisterPage;
import com.bbcare.automation.utils.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * [MODULE M2: KIỂM THỬ ĐĂNG KÝ TÀI KHOẢN]
 * Chạy thử lệnh: mvn test -Dtest=RegisterTest
 */
@DisplayName("M2 - Kiểm thử chức năng Đăng ký tài khoản")
class RegisterTest extends BaseTest {

    @ParameterizedTest(name = "{0}: email={1} -> expected={6}")
    @CsvFileSource(resources = "/testdata/register_data.csv", numLinesToSkip = 1, encoding = "UTF-8")
    void testRegisterDataDriven(String tcId, String email, String password, String fullName,
                                String phone, String address, String expectedResult, String expectedMessage) {
        String resolvedEmail = DataGenerator.resolve(email);
        RegisterPage registerPage = new RegisterPage(driver).open(BASE_URL);
        registerPage.register(resolvedEmail, password, fullName, phone, address);

        switch (expectedResult) {
            case "SUCCESS" -> {
                assertTrue(registerPage.isSuccessAlertDisplayed(), tcId + " phải hiển thị thông báo thành công");
                assertTrue(registerPage.getAlertMessage().contains(expectedMessage),
                        tcId + " nội dung thông báo không khớp: " + registerPage.getAlertMessage());
            }
            case "FAIL" -> {
                assertTrue(registerPage.isErrorAlertDisplayed(),
                        tcId + " phải chặn đăng ký và hiển thị khung báo lỗi (Nếu vẫn đăng ký thành công là BUG của hệ thống)");
                assertEquals(expectedMessage, registerPage.getAlertMessage(), tcId);
            }
            case "REQUIRED" -> {
                assertTrue(registerPage.getCurrentUrl().contains("/login"),
                        tcId + " form thiếu trường bắt buộc không được phép submit");
            }
            default -> fail("expectedResult không hợp lệ trong CSV: " + expectedResult);
        }
    }
}
