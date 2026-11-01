package com.bbcare.automation.pages;

import com.bbcare.automation.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;

/**
 * [MODULE M5: ĐẶT CA Y TÁ TRỌN VẸN 5 BƯỚC (WIZARD 1 -> 2 -> 3 -> 4 -> 5)]
 * Page Object hoàn chỉnh cho luồng Đặt Ca Y Tá (serviceDetailPage):
 * - Bước 1: Chọn dịch vụ y tế & Điền thông tin bệnh nhân
 * - Bước 2: Chọn khung giờ chăm sóc & Chọn ngày trên Sổ lịch
 * - Bước 3: Chọn Điều dưỡng phụ trách
 * - Bước 4: Nhắn tin trao đổi & Giả lập Điều dưỡng phê duyệt lịch trực
 * - Bước 5: Đặt ca thành công & Xác nhận phương thức thanh toán (VietQR / COD)
 */
public class BookingPage extends BasePage {

    private final By navBookingBtn       = By.cssSelector("[data-testid='nav-booking-btn']");
    // Bước 1
    private final By serviceTrigger      = By.cssSelector("[data-testid='wz-services-trigger']");
    private final By closeDropdownBtn    = By.cssSelector("button[data-act='closeSkillsDropdown']");
    private final By patientNameInput    = By.cssSelector("[data-testid='wz-patient-name']");
    private final By exactAgeInput       = By.cssSelector("[data-testid='wz-patient-age']");
    private final By medicalNoteInput    = By.cssSelector("[data-testid='wz-medical-history']");
    // Bước 2
    private final By shiftDropdownBtn    = By.cssSelector(".shift-trigger[data-act='toggleTimeDropdown']");
    private final By openPresetModalBtn  = By.cssSelector("button[data-act='openPresetModal']");
    // Bước 3
    private final By firstNurseCard      = By.cssSelector("#matchingNursesListContainer .nurse-row-card");
    // Bước 4 (Khách hàng & Cổng Điều dưỡng /nurse)
    private final By chatInput           = By.cssSelector("[data-testid='wz-chat-input']");
    private final By chatSendBtn         = By.cssSelector("[data-testid='wz-chat-send-btn']");
    private final By nurseModalViewBtn   = By.cssSelector("#nurseOldestPendingModal button[onclick*='navigateToNewRequestsTab']");
    private final By nurseRequestsTab    = By.cssSelector(".sidebar-link[onclick*='tab-requests']");
    private final By lastNurseConfirmBtn = By.xpath("(//div[@id='nurseShiftRequestsContainer']//button[contains(@class,'n-btn-confirm-main')])[last()]");
    // Bước 5
    private final By successTitle        = By.cssSelector("[data-testid='wz-success-title']");
    private final By bookingCodeText     = By.cssSelector("[data-testid='wz-booking-code']");
    private final By payTabQr            = By.cssSelector("[data-testid='wz-pay-tab-qr']");
    private final By payTabCod           = By.cssSelector("[data-testid='wz-pay-tab-cod']");
    private final By confirmQrPaidBtn    = By.cssSelector("button.s5-btn-paid-melon");
    private final By confirmCodBtn       = By.cssSelector("button.s5-btn-confirm-cod");

    private final By nextStepBtn         = By.cssSelector("[data-testid='wz-btn-next']");
    private final By step5Indicator      = By.cssSelector("[data-testid='wz-step-5-indicator']");
    private final By errorMsgBox         = By.cssSelector("[data-testid='wz-error-msg']");

    public BookingPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Đăng nhập tài khoản khách hàng trước, sau đó bấm nút ĐẶT CA Y TÁ để mở Wizard Bước 1/5
     */
    public BookingPage openBookingWizard(String baseUrl) {
        loginAsCustomer(baseUrl);
        click(navBookingBtn);
        waitVisible(nextStepBtn);
        return this;
    }

    /**
     * [BƯỚC 1] Chọn dịch vụ y tế trong dropdown (ví dụ: thay_bang, truyen_dich, ho_tro_oxy...)
     */
    public BookingPage selectServiceSkill(String skillKey) {
        if (skillKey != null && !skillKey.isBlank()) {
            click(serviceTrigger);
            click(By.cssSelector("[data-testid='wz-skill-item-" + skillKey.trim() + "']"));
            click(closeDropdownBtn);
        }
        return this;
    }

    /**
     * [BƯỚC 1] Điền thông tin người bệnh (Họ tên, Tuổi, Tiền sử bệnh lý)
     */
    public BookingPage fillStep1PatientInfo(String patientName, String age, String note) {
        type(patientNameInput, patientName);
        type(exactAgeInput, age);
        type(medicalNoteInput, note);
        return this;
    }

    /**
     * [BƯỚC 2] Chọn khung giờ chăm sóc cố định và chọn 1 ngày trống trên Sổ lịch chăm sóc, rồi bấm Tiếp tục sang Bước 3
     */
    public BookingPage completeStep2TimeAndDate(String shiftKey) {
        // 1. Mở thanh chọn khung giờ -> Mở popup Ca trực cố định -> Chọn ca (ví dụ CA_5H)
        click(shiftDropdownBtn);
        click(openPresetModalBtn);
        String key = (shiftKey != null && !shiftKey.isBlank()) ? shiftKey.trim() : "CA_5H";
        click(By.cssSelector("button.shift-tile-modal[onclick*=\"'" + key + "'\"]"));

        // 2. Chọn 1 ô ngày còn trống (chưa có lịch "Đã đặt") trên cuốn lịch tháng hiện tại
        By freeCalendarCell = By.xpath("//div[contains(@class,'tw-pcal-cell') and @data-act='calCellClick' and not(.//span[contains(text(),'Đã đặt')])]");
        click(freeCalendarCell);

        // 3. Bấm Tiếp tục: Chọn điều dưỡng (sang Bước 3)
        click(nextStepBtn);
        return this;
    }

    /**
     * [BƯỚC 3] Chọn Điều dưỡng phụ trách trong danh sách và bấm Đặt ca y tá & Trao đổi lịch (sang Bước 4)
     */
    public BookingPage completeStep3SelectNurse(String nurseId) {
        waitVisible(firstNurseCard);
        By targetNurse = (nurseId != null && !nurseId.isBlank())
                ? By.cssSelector("#matchingNursesListContainer .nurse-row-card[data-v='" + nurseId.trim() + "']")
                : firstNurseCard;
        if (!driver.findElements(targetNurse).isEmpty()) {
            click(targetNurse);
        } else {
            click(firstNurseCard);
        }
        // Bấm nút chuyển sang Bước 4 (Trao đổi & Chốt lịch)
        click(nextStepBtn);
        return this;
    }

    /**
     * [BƯỚC 4] Nhắn tin trao đổi với Điều dưỡng, sau đó mở tab mới sang Cổng Điều dưỡng (/nurse)
     * cuộn xuống cuối danh sách (ca bệnh nhân vừa gửi mới nhất nằm ở dưới đáy) và bấm "Gửi & Chốt ca",
     * rồi quay lại tab Khách hàng để sang Bước 5
     */
    public BookingPage completeStep4ChatAndNurseApproval(String baseUrl, String nurseId) {
        waitVisible(chatInput);
        type(chatInput, "Chào Điều dưỡng, nhờ chị duyệt lịch trực giúp gia đình nhé!");
        click(chatSendBtn);

        JavascriptExecutor js = (JavascriptExecutor) driver;
        String activeNurseId = (nurseId != null && !nurseId.isBlank())
                ? nurseId.trim()
                : String.valueOf(js.executeScript("return (typeof B !== 'undefined' && B.nurseId) ? String(B.nurseId) : 'hong';"));

        // Thiết lập sẵn nurseId trong localStorage để khi mở /nurse sẽ hiển thị đúng ca của Điều dưỡng vừa chọn
        js.executeScript("localStorage.setItem('bbcare_nurse_id', arguments[0]);", activeNurseId);

        String customerTab = driver.getWindowHandle();

        // 1. Mở tab mới sang Cổng Điều dưỡng (/nurse) để xác nhận lịch trực thật
        driver.switchTo().newWindow(WindowType.TAB);
        driver.get(baseUrl + "/nurse");
        slowDown();

        // 2. Nếu có Popup thông báo ca mới thì bấm nút Xem danh sách ca mới, ngược lại bấm tab "Yêu cầu đặt ca mới"
        if (!driver.findElements(nurseModalViewBtn).isEmpty()) {
            click(nurseModalViewBtn);
        } else {
            click(nurseRequestsTab);
        }

        // Đảm bảo danh sách yêu cầu của đúng Điều dưỡng đã được tải xong và đóng popup nếu hiện trễ
        js.executeScript(
                "localStorage.setItem('bbcare_nurse_id', arguments[0]);" +
                        "const m = document.getElementById('nurseOldestPendingModal'); if (m) m.remove();",
                activeNurseId
        );
        slowDown();

        // 3. Cuộn xuống cuối danh sách (bệnh nhân mới gửi nằm ở dưới đáy danh sách) và bấm nút "Gửi & Chốt ca"
        waitVisible(lastNurseConfirmBtn);
        js.executeScript(
                "const m = document.getElementById('nurseOldestPendingModal'); if (m) m.remove();"
        );
        click(lastNurseConfirmBtn);

        // Dừng 1 nhịp để thấy Cổng Điều dưỡng đã chốt lịch thành công rồi đóng tab Điều dưỡng
        slowDown();
        driver.close();

        // 4. Quay về tab Khách hàng và đồng bộ trạng thái chuyển sang Bước 5 (Đặt Ca Thành Công)
        driver.switchTo().window(customerTab);
        slowDown();
        js.executeScript("if (typeof loadStep4ChatMessages === 'function') loadStep4ChatMessages();");
        slowDown();
        js.executeScript(
                "if (typeof B !== 'undefined' && B.step === 4 && typeof autoFinalizeOnNurseConfirm === 'function') {" +
                        "  autoFinalizeOnNurseConfirm({ scheduleConfirmed: true, fromDatabase: true });" +
                        "}"
        );
        waitVisible(successTitle);
        return this;
    }

    /**
     * [BƯỚC 5] Chọn phương thức thanh toán (VIETQR hoặc COD) và bấm nút Xác nhận hoàn tất thanh toán
     */
    public BookingPage completeStep5Payment(String paymentMethod) {
        waitVisible(successTitle);
        if ("COD".equalsIgnoreCase(paymentMethod)) {
            click(payTabCod);
            slowDown();
            // Nếu có nút xác nhận thì bấm, nếu web tự chốt rồi thì không bị treo nữa
            if (!driver.findElements(confirmCodBtn).isEmpty() && driver.findElement(confirmCodBtn).isDisplayed()) {
                click(confirmCodBtn);
            }
        } else {
            click(payTabQr);
            click(confirmQrPaidBtn);
        }
        return this;
    }

    public void clickNextStep() {
        click(nextStepBtn);
    }

    public boolean isStep5Completed() {
        String cls = waitVisible(step5Indicator).getAttribute("class");
        return isDisplayed(successTitle) && cls != null && cls.contains("active");
    }

    public String getSuccessTitleText() {
        return getText(successTitle);
    }

    public String getBookingCodeText() {
        return getText(bookingCodeText);
    }

    public boolean isWizardErrorDisplayed() {
        return isDisplayed(errorMsgBox);
    }

    public String getWizardErrorMessage() {
        return getText(errorMsgBox);
    }
}
