package com.bbcare.automation.pages;

import com.bbcare.automation.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ProfileAndFeedbackPage extends BasePage {

    // --- Các WebElement cho chức năng Hủy lịch hẹn ---
    private final By cancelModalCodeHeading = By.cssSelector(".cancel-modal-code");
    private final By bankCodeDropdown = By.cssSelector("select[name='bankCode']");
    private final By accountNumberInput = By.cssSelector("input[name='accountNumber']");
    private final By accountHolderInput = By.cssSelector("input[name='accountHolder']");
    private final By cancelReasonInput = By.cssSelector("textarea[name='cancelReason']");
    private final By confirmCancelBtn = By.cssSelector("button.btn-confirm-cancel");
    private final By keepAppointmentBtn = By.cssSelector("button.btn-keep-appt");
    private final By cancelModal = By.cssSelector(".cancel-appointment-modal");

    // --- Các WebElement chung & Chức năng Đánh giá / Khiếu nại ---
    private final By toastMessage = By.cssSelector(".toast-message");
    private final By completedShiftList = By.cssSelector(".completed-shift-list");
    private final By feedbackErrorMsg = By.cssSelector(".error-message-feedback");

    public ProfileAndFeedbackPage(WebDriver driver) {
        super(driver);
    }

    // --- Nhóm phương thức: Hủy lịch hẹn ---
    public ProfileAndFeedbackPage openProfilePage(String baseUrl) {
        driver.get(baseUrl + "/profile");
        return this;
    }

    public ProfileAndFeedbackPage openCancelModalForCode(String apptCode) {
        By cancelBtnForCode = By.xpath(String.format("//*[contains(text(), '%s')]/ancestor::div[contains(@class, 'appointment-item')]//button[contains(text(), 'Hủy')]", apptCode));
        click(cancelBtnForCode);
        return this;
    }

    public String getCancelModalCodeText() {
        return getText(cancelModalCodeHeading);
    }

    public void fillCancelRefundForm(String bankCode, String accountNumber, String accountHolder, String cancelReasonValue) {
        type(accountNumberInput, accountNumber);
        type(accountHolderInput, accountHolder);
        type(cancelReasonInput, cancelReasonValue);
    }

    public void clickConfirmCancel() {
        click(confirmCancelBtn);
    }

    public void clickKeepAppointment() {
        click(keepAppointmentBtn);
    }

    public boolean isCancelModalDisplayed() {
        return isDisplayed(cancelModal);
    }

    // --- Nhóm phương thức: Toast Message chung ---
    public boolean isToastDisplayed() {
        return isDisplayed(toastMessage);
    }

    public String getToastMessageText() {
        return getText(toastMessage);
    }

    // --- Nhóm phương thức: Đánh giá điều dưỡng & Gửi khiếu nại ---
    public ProfileAndFeedbackPage openCompletedShiftRatingModal(String baseUrl) {
        driver.get(baseUrl + "/profile/completed-shifts");
        return this;
    }

    public void submitFeedbackForm(String category, int starRating, String senderName, String senderPhone, String content) {
        // Thêm code tương tác chọn danh mục, số sao, điền thông tin và bấm gửi đánh giá tại đây
    }

    public ProfileAndFeedbackPage reopenCompletedShiftsModalToVerify() {
        // Mở lại mục ca trực đã kết thúc để load lịch sử hiển thị
        return this;
    }

    public String getCompletedShiftsListText() {
        return getText(completedShiftList);
    }

    public boolean isFeedbackErrorDisplayed() {
        return isDisplayed(feedbackErrorMsg);
    }

    public String getFeedbackErrorMessage() {
        return getText(feedbackErrorMsg);
    }
}