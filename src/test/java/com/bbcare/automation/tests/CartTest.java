package com.bbcare.automation.tests;

import com.bbcare.automation.base.BaseTest;
import com.bbcare.automation.pages.CartPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * [MODULE M4: KIỂM THỬ GIỎ HÀNG THIẾT BỊ Y TẾ]
 * Chạy thử lệnh: mvn test -Dtest=CartTest
 */
@DisplayName("M4 - Kiểm thử Giỏ hàng Thiết bị Y tế")
class CartTest extends BaseTest {

    @ParameterizedTest(name = "{0}: productId={1}, action={2}, clicks={3} -> expectedQty={4}")
    @CsvFileSource(resources = "/testdata/cart_data.csv", numLinesToSkip = 1, encoding = "UTF-8")
    void testCartOperationsDataDriven(String tcId, String productId, String actionType,
                                      int clickTimes, int expectedQty, String expectedMessage) {
        CartPage cartPage = new CartPage(driver).openStore(BASE_URL);

        // Trường hợp sản phẩm hết hàng (stock = 0, ví dụ p_out_of_stock)
        if ("OUT_OF_STOCK".equalsIgnoreCase(expectedMessage) || expectedQty == 0) {
            if ("REMOVE".equalsIgnoreCase(actionType)) {
                // Thêm 1 lần vào giỏ rồi mở giỏ xóa đi
                cartPage.addProductByType(productId, "buy", 1);
                cartPage.openCartTab();
                cartPage.removeItemAtRow(0);
                assertTrue(cartPage.isCartEmptyDisplayed(), tcId + " sau khi xóa phải hiển thị giỏ hàng trống");
                assertEquals(0, cartPage.getCartBadgeCount(), tcId + " badge trên Navbar phải về 0");
            } else {
                // Kiểm tra nút Mua bị vô hiệu hóa khi hết hàng (RULE-CART-07)
                assertTrue(cartPage.isStoreProductButtonDisabled(productId, actionType),
                        tcId + " sản phẩm hết hàng thì nút bấm phải bị disabled");
                assertTrue(cartPage.getStoreProductButtonText(productId, actionType).contains("Hết Hàng"),
                        tcId + " nút bấm phải hiển thị chữ 'Hết Hàng'");
            }
            return;
        }

        // Trường hợp thêm sản phẩm hợp lệ và kiểm tra cộng dồn số lượng trong giỏ (RULE-CART-03)
        cartPage.addProductByType(productId, actionType, clickTimes);
        cartPage.openCartTab();

        assertEquals(expectedQty, cartPage.getItemQtyAtRow(0),
                tcId + " số lượng cộng dồn của sản phẩm trong giỏ phải đúng bằng " + expectedQty);
        assertEquals(expectedQty, cartPage.getCartBadgeCount(),
                tcId + " số lượng hiển thị trên badge Navbar phải khớp");
    }
}

