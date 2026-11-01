package com.bbcare.automation.pages;

import com.bbcare.automation.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * [MODULE M4: GIỎ HÀNG & ĐẶT HÀNG CHECKOUT]
 * Page Object hoàn chỉnh cho màn hình Giỏ hàng (cartPage) và Modal Đặt hàng (checkoutOrderModal).
 */
public class CartPage extends BasePage {

    private final By navStoreBtn         = By.cssSelector("[data-testid='nav-store-btn']");
    private final By navCartBtn          = By.cssSelector("[data-testid='nav-cart-btn']");
    private final By navCartCount        = By.cssSelector("[data-testid='nav-cart-count']");
    private final By emptyCartMsg        = By.cssSelector("[data-testid='cart-empty-msg']");
    private final By cartTotalPrice      = By.cssSelector("[data-testid='cart-total-price']");
    private final By openCheckoutBtn     = By.cssSelector("[data-testid='btn-open-checkout']");

    // Các phần tử trong Modal Đặt hàng (Checkout)
    private final By checkoutNameInput   = By.cssSelector("[data-testid='checkout-name-input']");
    private final By checkoutPhoneInput  = By.cssSelector("[data-testid='checkout-phone-input']");
    private final By checkoutAddrInput   = By.cssSelector("[data-testid='checkout-address-input']");
    private final By checkoutSubmitBtn   = By.cssSelector("[data-testid='checkout-submit-btn']");
    private final By checkoutErrorMsg    = By.cssSelector("[data-testid='checkout-error-msg']");
    private final By toastMessage        = By.cssSelector("[data-testid='toast-message']");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public CartPage openStore(String baseUrl) {
        loginAsCustomer(baseUrl);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "if (typeof cart !== 'undefined') { cart.length = 0; } if (typeof updateSummary === 'function') { updateSummary(); }");
        click(navStoreBtn);
        slowDown();
        return this;
    }

    public CartPage openStoreAfterLogin(String baseUrl) {
        loginAsCustomer(baseUrl);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "if (typeof cart !== 'undefined') { cart.length = 0; } if (typeof updateSummary === 'function') { updateSummary(); }");
        click(navStoreBtn);
        slowDown();
        return this;
    }

    public CartPage addProductByType(String productId, String actionType, int times) {
        By exactBtn = By.cssSelector("[data-testid='store-btn-" + actionType + "-" + productId + "']");
        By fallbackBtn = By.cssSelector("button[data-testid^='store-btn-" + actionType + "-']:not([disabled])");
        By targetBtn = !driver.findElements(exactBtn).isEmpty() ? exactBtn : fallbackBtn;
        for (int i = 0; i < times; i++) {
            click(targetBtn);
        }
        return this;
    }

    public boolean isStoreProductButtonDisabled(String productId, String actionType) {
        By btn = By.cssSelector("[data-testid='store-btn-" + actionType + "-" + productId + "']");
        return !waitVisible(btn).isEnabled();
    }

    public String getStoreProductButtonText(String productId, String actionType) {
        By btn = By.cssSelector("[data-testid='store-btn-" + actionType + "-" + productId + "']");
        return getText(btn);
    }

    public CartPage openCartTab() {
        click(navCartBtn);
        return this;
    }

    public int getCartBadgeCount() {
        return Integer.parseInt(getText(navCartCount));
    }

    public int getItemQtyAtRow(int rowIndex) {
        By qtyBy = By.cssSelector("[data-testid='cart-item-qty-" + rowIndex + "']");
        return Integer.parseInt(getText(qtyBy));
    }

    public void clickIncreaseAtRow(int rowIndex) {
        click(By.cssSelector("[data-testid='cart-btn-inc-" + rowIndex + "']"));
    }

    public void clickDecreaseAtRow(int rowIndex) {
        click(By.cssSelector("[data-testid='cart-btn-dec-" + rowIndex + "']"));
    }

    public void removeItemAtRow(int rowIndex) {
        click(By.cssSelector("[data-testid='cart-btn-remove-" + rowIndex + "']"));
    }

    public boolean isCartEmptyDisplayed() {
        return isDisplayed(emptyCartMsg);
    }

    public String getCartTotalText() {
        return getText(cartTotalPrice);
    }

    public CartPage openCheckoutModal() {
        click(openCheckoutBtn);
        waitVisible(checkoutNameInput);
        return this;
    }

    public void submitCheckoutForm(String receiverName, String phone, String address) {
        type(checkoutNameInput, receiverName);
        type(checkoutPhoneInput, phone);
        type(checkoutAddrInput, address);
        click(checkoutSubmitBtn);
    }

    public boolean isCheckoutErrorDisplayed() {
        return isDisplayed(checkoutErrorMsg);
    }

    public String getCheckoutErrorMessage() {
        return getText(checkoutErrorMsg);
    }

    public boolean isToastDisplayed() {
        return isDisplayed(toastMessage);
    }

    public String getToastMessage() {
        return getText(toastMessage);
    }
}

