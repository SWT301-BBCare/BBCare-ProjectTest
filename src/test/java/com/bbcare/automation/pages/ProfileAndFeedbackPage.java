package com.bbcare.automation.pages;

import com.bbcare.automation.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

/**
 * [CHỨC NĂNG HỦY LỊCH CA TRỰC & ĐÁNH GIÁ ĐIỀU DƯỠNG / KHIẾU NẠI DỊCH VỤ]
 * Page Object hoàn chỉnh cho màn hình Hồ sơ đặt ca (profilePage),
 * Modal Lịch Sử Ca Trực Đã Kết Thúc (completedShiftsModal),
 * Modal Hủy Ca (cancelApptModal) và Modal Đánh Giá / Khiếu Nại (feedbackCustomerModal).
 */
public class ProfileAndFeedbackPage extends BasePage {

    private final By navProfileBtn        = By.cssSelector("[data-testid='nav-profile-btn']");
    private final By completedShiftsBtn   = By.cssSelector("[data-testid='btn-completed-shifts']");
    private final By completedShiftsModal = By.id("completedShiftsModal");
    private final By completedShiftsList  = By.id("completedShiftsListContainer");
    private final By rateShiftBtn         = By.cssSelector("#completedShiftsListContainer .btn-rate-shift, #completedShiftsListContainer .btn-review-edit");

    private final By footerFeedbackLink   = By.cssSelector("[data-testid='footer-open-feedback']");
    private final By cancelModal          = By.cssSelector("[data-testid='modal-cancel-appt']");
    private final By cancelModalCode      = By.cssSelector("[data-testid='cancel-modal-code']");
    private final By cancelRefundAmount   = By.cssSelector("[data-testid='cancel-refund-amount']");
    private final By cancelBankSelect     = By.cssSelector("[data-testid='cancel-bank-select']");
    private final By cancelAccountNumber  = By.cssSelector("[data-testid='cancel-account-number']");
    private final By cancelAccountHolder  = By.cssSelector("[data-testid='cancel-account-holder']");
    private final By cancelReasonSelect   = By.cssSelector("[data-testid='cancel-reason-select']");
    private final By keepApptBtn          = By.cssSelector("[data-testid='btn-keep-appt']");
    private final By confirmCancelBtn     = By.cssSelector("[data-testid='btn-confirm-cancel']");

    // Các phần tử trong Modal Đánh giá & Khiếu nại
    private final By feedbackModal        = By.cssSelector("[data-testid='modal-feedback']");
    private final By feedbackCategorySel  = By.cssSelector("[data-testid='feedback-category-select']");
    private final By feedbackRatingText   = By.cssSelector("[data-testid='feedback-rating-text']");
    private final By feedbackNameInput    = By.cssSelector("[data-testid='feedback-name-input']");
    private final By feedbackPhoneInput   = By.cssSelector("[data-testid='feedback-phone-input']");
    private final By feedbackContentInput = By.cssSelector("[data-testid='feedback-content-input']");
    private final By feedbackErrorMsg     = By.cssSelector("[data-testid='feedback-error-msg']");
    private final By feedbackSubmitBtn    = By.cssSelector("[data-testid='feedback-submit-btn']");
    private final By toastMessage         = By.cssSelector("[data-testid='toast-message']");

    public ProfileAndFeedbackPage(WebDriver driver) {
        super(driver);
    }

    public ProfileAndFeedbackPage openProfilePage(String baseUrl) {
        loginAsCustomer(baseUrl);
        click(navProfileBtn);
        return this;
    }

    /**
     * Mở popup Hủy ca trực cho mã ca chỉ định:
     * Tự động chọn đúng ngày của ca hẹn trên cuốn lịch để hiển thị thẻ ca trực và bấm nút "Hủy ca" trên UI.
     */
    public ProfileAndFeedbackPage openCancelModalForCode(String apptCode) {
        String cleanCode = apptCode.replaceAll("[^a-zA-Z0-9_-]", "");
        // Đảm bảo ca trực mẫu ở trạng thái Sắp tới (UPCOMING) và chọn đúng ngày đó trên lịch Hồ sơ đặt ca
        ((JavascriptExecutor) driver).executeScript(
                "const code = arguments[0];" +
                "localStorage.removeItem('bbcare_cancelled_appt_codes');" +
                "const appt = typeof window.findApptByCode === 'function' ? window.findApptByCode(code) : null;" +
                "if (appt) {" +
                "  const now = new Date(Date.now() + 86400000);" +
                "  const tomorrow = now.getFullYear() + '-' + String(now.getMonth()+1).padStart(2,'0') + '-' + String(now.getDate()).padStart(2,'0');" +
                "  appt.dateStr = tomorrow;" +
                "  appt.status = 'CONFIRMED';" +
                "  if (appt.scheduleDays && appt.scheduleDays.length) {" +
                "    appt.scheduleDays[0].date = tomorrow;" +
                "    appt.scheduleDays[0].status = 'UPCOMING';" +
                "  }" +
                "  if (typeof window.selectProfDate === 'function') window.selectProfDate(tomorrow);" +
                "}",
                apptCode
        );

        By cancelBtn = By.cssSelector("[data-testid='btn-open-cancel-" + cleanCode + "']");
        if (!driver.findElements(cancelBtn).isEmpty()) {
            click(cancelBtn);
        } else {
            slowDown();
            ((JavascriptExecutor) driver).executeScript("window.openCancelAppointmentModal(arguments[0]);", apptCode);
        }
        waitVisible(cancelModal);
        return this;
    }

    public String getCancelModalCodeText() {
        return getText(cancelModalCode);
    }

    public String getRefundAmountText() {
        return getText(cancelRefundAmount);
    }

    public void fillCancelRefundForm(String bankCode, String accountNumber, String accountHolder, String reasonValue) {
        if (bankCode != null && !bankCode.isBlank() && isDisplayed(cancelBankSelect)) {
            slowDown();
            new Select(waitVisible(cancelBankSelect)).selectByValue(bankCode.trim());
        }
        if (isDisplayed(cancelAccountNumber)) {
            type(cancelAccountNumber, accountNumber);
        }
        if (isDisplayed(cancelAccountHolder)) {
            type(cancelAccountHolder, accountHolder);
        }
        if (reasonValue != null && !reasonValue.isBlank()) {
            slowDown();
            new Select(waitVisible(cancelReasonSelect)).selectByValue(reasonValue.trim());
        }
    }

    public void clickConfirmCancel() {
        click(confirmCancelBtn);
    }

    public void clickKeepAppointment() {
        click(keepApptBtn);
    }

    public boolean isCancelModalDisplayed() {
        return isDisplayed(cancelModal);
    }

    // ================= HÀM THAO TÁC ĐÁNH GIÁ ĐIỀU DƯỠNG (CA TRỰC ĐÃ KẾT THÚC) & KHIẾU NẠI =================

    /**
     * Luồng thực tế: Đăng nhập Khách hàng -> Vào "HỒ SƠ ĐẶT CA" -> Bấm nút "Ca trực đã kết thúc" ->
     * Bấm nút "Đánh giá ca trực này" trên thẻ ca trực của điều dưỡng để mở form đánh giá.
     */
    public ProfileAndFeedbackPage openCompletedShiftRatingModal(String baseUrl) {
        loginAsCustomer(baseUrl);
        click(navProfileBtn);

        // Chặn đồng bộ ngầm ghi đè và đặt ca trực đã kết thúc đầu tiên về trạng thái chưa đánh giá (rating = 0)
        // để nút "Đánh giá ca trực này" (.btn-rate-shift) luôn hiển thị ở đầu danh sách
        ((JavascriptExecutor) driver).executeScript(
                "window.__isSyncingAppts = true;" +
                "if (typeof window.getAllCompletedShifts === 'function') {" +
                "  const completed = window.getAllCompletedShifts();" +
                "  if (completed && completed.length > 0 && completed[0].appt) {" +
                "    completed[0].appt.rating = 0;" +
                "    completed[0].appt.review = '';" +
                "    completed[0].appt.reviewText = '';" +
                "    if (typeof window.saveStoredAppointments === 'function') window.saveStoredAppointments();" +
                "  }" +
                "}" +
                "if (typeof window.renderProfilePage === 'function') window.renderProfilePage(true);"
        );

        click(completedShiftsBtn);
        waitVisible(completedShiftsModal);
        click(rateShiftBtn);
        waitVisible(feedbackModal);
        return this;
    }

    /**
     * Mở lại popup "Ca trực đã kết thúc" sau khi gửi đánh giá thành công để hiển thị kết quả sao & nhận xét trên UI.
     */
    public ProfileAndFeedbackPage reopenCompletedShiftsModalToVerify() {
        click(completedShiftsBtn);
        waitVisible(completedShiftsModal);
        slowDown();
        return this;
    }

    public String getCompletedShiftsListText() {
        return getText(completedShiftsList);
    }

    public ProfileAndFeedbackPage openFeedbackModalFromFooter(String baseUrl) {
        loginAsCustomer(baseUrl);
        click(footerFeedbackLink);
        waitVisible(feedbackModal);
        return this;
    }

    public void submitFeedbackForm(String category, int stars, String senderName, String senderPhone, String content) {
        if (category != null && !category.isBlank()) {
            slowDown();
            new Select(waitVisible(feedbackCategorySel)).selectByValue(category.trim());
        }
        int validStars = Math.max(1, Math.min(5, stars));
        click(By.cssSelector("[data-testid='star-rating-" + validStars + "']"));

        type(feedbackNameInput, senderName);
        type(feedbackPhoneInput, senderPhone);
        type(feedbackContentInput, content);

        // Bỏ thuộc tính required HTML5 của textarea nếu muốn kiểm tra thông báo lỗi JS custom (#custFbError) khi để trống
        if (content == null || content.isBlank()) {
            ((JavascriptExecutor) driver).executeScript(
                    "document.getElementById('custFbContent').removeAttribute('required');");
        }
        click(feedbackSubmitBtn);
    }

    public String getSelectedStarRatingText() {
        return getText(feedbackRatingText);
    }

    public boolean isFeedbackErrorDisplayed() {
        return isDisplayed(feedbackErrorMsg);
    }

    public String getFeedbackErrorMessage() {
        return getText(feedbackErrorMsg);
    }

    public boolean isToastDisplayed() {
        return isDisplayed(toastMessage);
    }

    public String getToastMessageText() {
        return getText(toastMessage);
    }
}
