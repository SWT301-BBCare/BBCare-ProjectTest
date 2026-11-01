package com.bbcare.automation.tests;

import com.bbcare.automation.base.BaseTest;
import com.bbcare.automation.pages.BookingPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Issue #7 - Automation Test Đặt lịch & Thanh toán")
public class BookingPaymentTest extends BaseTest {

    @ParameterizedTest(name = "{0}: skill={1}, nurse={6}, pay={7} -> expected={8}")
    @CsvFileSource(resources = "/testdata/nurse_booking_data.csv", numLinesToSkip = 1, encoding = "UTF-8")
    void testNurseBookingAndPaymentDataDriven(String tcId, String skillKey, String patientName,
                                              String age, String medicalNote,
                                              String shiftKey, String nurseId, String paymentMethod,
                                              String expectedResult, String expectedMessage) {
        // Mở trang Đặt ca y tá sau khi đăng nhập
        BookingPage bookingPage = new BookingPage(driver).openBookingWizard(BASE_URL);

        // Bước 1: Chọn dịch vụ & nhập thông tin bệnh nhân
        bookingPage.selectServiceSkill(skillKey)
                .fillStep1PatientInfo(patientName, age, medicalNote)
                .clickNextStep();

        switch (expectedResult) {
            case "SUCCESS" -> {
                // Đi qua Bước 2 -> Bước 3 -> Bước 4 -> Bước 5 (Thanh toán)
                bookingPage.completeStep2TimeAndDate(shiftKey)
                        .completeStep3SelectNurse(nurseId)
                        .completeStep4ChatAndNurseApproval(BASE_URL, nurseId)
                        .completeStep5Payment(paymentMethod);

                // Nghiệm thu kết quả
                assertTrue(bookingPage.isStep5Completed(), tcId + " phải hoàn tất tới Bước 5");
                assertEquals(expectedMessage, bookingPage.getSuccessTitleText(), tcId + " tiêu đề phải đúng");
                assertTrue(bookingPage.getBookingCodeText().startsWith("#"), tcId + " phải sinh mã đơn #BK-...");
            }
            case "FAIL" -> {
                // Kiểm tra validation bắt lỗi khi thiếu thông tin
                assertTrue(bookingPage.isWizardErrorDisplayed(), tcId + " phải hiện thông báo lỗi");
                assertTrue(bookingPage.getWizardErrorMessage().contains(expectedMessage), tcId + " nội dung lỗi không khớp");
            }
            default -> fail("expectedResult trong CSV không hợp lệ: " + expectedResult);
        }
    }
}