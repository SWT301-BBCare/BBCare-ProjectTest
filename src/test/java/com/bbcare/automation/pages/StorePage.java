package com.bbcare.automation.pages;

import com.bbcare.automation.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * [MODULE M3: TÌM KIẾM & LỌC SẢN PHẨM CỬA HÀNG Y TẾ]
 * Page Object hoàn chỉnh cho khu vực Cửa Hàng Y Tế (storePage trong index.html).
 */
public class StorePage extends BasePage {

    private final By navStoreBtn     = By.cssSelector("[data-testid='nav-store-btn']");
    private final By searchInput     = By.cssSelector("[data-testid='store-search-input']");
    private final By productCards    = By.cssSelector("#storeProductGrid .store-card-item");
    private final By emptyMessageBox = By.cssSelector("[data-testid='store-empty-msg']");
    private final By emptyTitle      = By.cssSelector("[data-testid='store-empty-title']");

    public StorePage(WebDriver driver) {
        super(driver);
    }

    public StorePage open(String baseUrl) {
        driver.get(baseUrl + "/");
        click(navStoreBtn);
        waitVisible(searchInput);
        return this;
    }

    public StorePage selectCategory(String categoryCode) {
        if (categoryCode != null && !categoryCode.isBlank()) {
            By catBtn = By.cssSelector("[data-testid='store-cat-" + categoryCode.trim() + "']");
            click(catBtn);
        }
        return this;
    }

    public StorePage searchKeyword(String keyword) {
        type(searchInput, keyword);
        return this;
    }

    public int getDisplayedProductCount() {
        List<WebElement> cards = driver.findElements(productCards);
        return cards.size();
    }

    public boolean isEmptyMessageDisplayed() {
        return isDisplayed(emptyMessageBox);
    }

    public String getEmptyMessageTitle() {
        return getText(emptyTitle);
    }

    public boolean isProductActionDisabled(String productId, String actionType) {
        By btn = By.cssSelector("[data-testid='store-btn-" + actionType + "-" + productId + "']");
        WebElement el = waitVisible(btn);
        return !el.isEnabled() || el.getAttribute("disabled") != null;
    }

    public void addProductFromStore(String productId, String actionType) {
        By btn = By.cssSelector("[data-testid='store-btn-" + actionType + "-" + productId + "']");
        click(btn);
    }
}
