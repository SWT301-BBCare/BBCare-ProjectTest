package com.bbcare.automation.pages;

import com.bbcare.automation.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Component dùng chung cho thanh điều hướng (Navbar) ở đầu trang index.html.
 * Gợi ý các data-testid đã được gắn sẵn:
 * - [data-testid='nav-home-btn']
 * - [data-testid='nav-booking-btn']
 * - [data-testid='nav-profile-btn']
 * - [data-testid='nav-store-btn']
 * - [data-testid='nav-cart-btn']
 * - [data-testid='nav-cart-count']
 * - [data-testid='nav-account-btn']
 */
public class NavbarComponent extends BasePage {

    private final By storeTabBtn = By.cssSelector("[data-testid='nav-store-btn']");
    private final By cartTabBtn  = By.cssSelector("[data-testid='nav-cart-btn']");
    private final By cartCount   = By.cssSelector("[data-testid='nav-cart-count']");

    public NavbarComponent(WebDriver driver) {
        super(driver);
    }

    public void goToStore() {
        click(storeTabBtn);
    }

    public void goToCart() {
        click(cartTabBtn);
    }

    public int getCartBadgeCount() {
        return Integer.parseInt(getText(cartCount));
    }
}

