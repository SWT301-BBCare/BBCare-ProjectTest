package com.bbcare.automation.tests;

import com.bbcare.automation.base.BaseTest;
import com.bbcare.automation.pages.ProfileAndFeedbackPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * [KIỂM THỬ CHỨC NĂNG HỦY LỊCH CA TRỰC & HOÀN TIỀN 70%]
 * Chạy thử lệnh: mvn test -Dtest=CancelAppointmentTest
 */
@DisplayName("Kiểm thử Hủy lịch ca trực & Hoàn tiền 70%")
class CancelAppointmentTest extends BaseTest {

    @ParameterizedTest(name = "{0}: apptCode={1}, bank={2} -> expected={6}")
    @CsvFileSource(resources = "/testdata/cancel_appointment_data.csv", numLinesToSkip = 1, encoding = "UTF-8")
    void testCancelAppointmentDataDriven(String tcId, String apptCode, String bankCode,
                                         String accountNumber, String accountHolder,
                                         String cancelReasonValue, String expectedResult, String expectedMessage) {
        com.bbcare.automation.pages.ProfileAndFeedbackPage page = new ProfileAndFeedbackPage(driver)
                .openProfilePage(BASE_URL)
                .openCancelModalForCode(apptCode);

        // Kiểm tra mã ca trên tiêu đề popup hủy lịch
        assertEquals(apptCode, page.getCancelModalCodeText(), tcId + " mã ca trên popup hủy lịch phải khớp");

        // Điền thông tin ngân hàng hoàn tiền & lý do hủy ca
        page.fillCancelRefundForm(bankCode, accountNumber, accountHolder, cancelReasonValue);

        switch (expectedResult) {
            case "SUCCESS" -> {
                page.clickConfirmCancel();
                assertTrue(page.isToastDisplayed(), tcId + " sau khi xác nhận hủy ca phải hiển thị thông báo Toast");
                assertTrue(page.getToastMessageText().contains(expectedMessage),
                        tcId + " nội dung thông báo hủy ca không khớp: " + page.getToastMessageText());
            }
            case "KEEP" -> {
                page.clickKeepAppointment();
                assertFalse(page.isCancelModalDisplayed(), tcId + " bấm Giữ lại ca thì popup hủy lịch phải đóng lại");
            }
            default -> fail("expectedResult không hợp lệ trong CSV: " + expectedResult);
        }
    }
}
