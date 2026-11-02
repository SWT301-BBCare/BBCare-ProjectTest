package com.bbcare.automation.tests;

import com.bbcare.automation.base.BaseTest;
import com.bbcare.automation.pages.ProfileAndFeedbackPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * [KIỂM THỬ CHỨC NĂNG ĐÁNH GIÁ ĐIỀU DƯỠNG (CA TRỰC ĐÃ KẾT THÚC) & GỬI KHIẾU NẠI]
 * Luồng kiểm thử: Đăng nhập -> Vào "Hồ sơ đặt ca" -> Bấm "Ca trực đã kết thúc" ->
 * Bấm "Đánh giá ca trực này" của Điều dưỡng -> Chọn số sao, nhập nhận xét & gửi đánh giá ->
 * Mở lại "Ca trực đã kết thúc" để kiểm chứng đánh giá đã hiển thị trên thẻ ca trực.
 */
@DisplayName("Kiểm thử Đánh giá điều dưỡng ở Ca trực đã kết thúc (Hồ sơ đặt ca) & Gửi khiếu nại")
class FeedbackTest extends BaseTest {

    @ParameterizedTest(name = "{0}: stars={2}, category={1} -> expected={6}")
    @CsvFileSource(resources = "/testdata/feedback_data.csv", numLinesToSkip = 1, encoding = "UTF-8")
    void testFeedbackAndRatingDataDriven(String tcId, String category, int starRating,
                                         String senderName, String senderPhone, String content,
                                         String expectedResult, String expectedMessage) {
        ProfileAndFeedbackPage page = new ProfileAndFeedbackPage(driver).openCompletedShiftRatingModal(BASE_URL);
        page.submitFeedbackForm(category, starRating, senderName, senderPhone, content);

        switch (expectedResult) {
            case "SUCCESS" -> {
                assertTrue(page.isToastDisplayed(), tcId + " gửi đánh giá thành công phải hiển thị thông báo Toast");
                assertTrue(page.getToastMessageText().contains(expectedMessage),
                        tcId + " thông báo không khớp: " + page.getToastMessageText());

                // Mở lại mục "Ca trực đã kết thúc" trong Hồ sơ đặt ca để hiển thị và kiểm chứng đánh giá điều dưỡng
                page.reopenCompletedShiftsModalToVerify();
                assertTrue(page.getCompletedShiftsListText().contains(content),
                        tcId + " nội dung đánh giá điều dưỡng phải hiển thị trong Lịch sử ca trực đã kết thúc");
            }
            case "FAIL" -> {
                assertTrue(page.isFeedbackErrorDisplayed(),
                        tcId + " để trống nội dung đánh giá phải hiển thị khung báo lỗi");
                assertEquals(expectedMessage, page.getFeedbackErrorMessage(), tcId);
            }
            default -> fail("expectedResult không hợp lệ trong CSV: " + expectedResult);
        }
    }
}
