package com.bbcare.automation.tests;

import com.bbcare.automation.base.BaseTest;
import com.bbcare.automation.pages.StorePage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * [MODULE M3: KIỂM THỬ TÌM KIẾM VÀ LỌC THIẾT BỊ Y TẾ]
 * Chạy thử lệnh: mvn test -Dtest=SearchTest
 */
@DisplayName("M3 - Kiểm thử Tìm kiếm và Lọc sản phẩm Cửa hàng Y tế")
class SearchTest extends BaseTest {

    @ParameterizedTest(name = "{0}: keyword={1}, cat={2} -> expected={3}")
    @CsvFileSource(resources = "/testdata/search_data.csv", numLinesToSkip = 1, encoding = "UTF-8")
    void testSearchAndFilterDataDriven(String tcId, String keyword, String category,
                                       String expectedResult, int expectedMinCount) {
        StorePage storePage = new StorePage(driver).open(BASE_URL);
        storePage.selectCategory(category);
        if (keyword != null) {
            storePage.searchKeyword(keyword);
        }

        switch (expectedResult) {
            case "FOUND" -> {
                int count = storePage.getDisplayedProductCount();
                assertTrue(count >= expectedMinCount,
                        tcId + " mong đợi tìm thấy >= " + expectedMinCount + " sản phẩm nhưng thực tế = " + count);
            }
            case "NOT_FOUND" -> {
                assertTrue(storePage.isEmptyMessageDisplayed(),
                        tcId + " phải hiển thị thông báo 'Không tìm thấy thiết bị y tế phù hợp'");
                assertEquals(0, storePage.getDisplayedProductCount(), tcId);
            }
            case "SAFE" -> {
                // Kiểm tra XSS/SQLi không làm sập trang và hiển thị thông báo không tìm thấy an toàn
                assertTrue(storePage.isEmptyMessageDisplayed(),
                        tcId + " input nguy hiểm phải được xử lý an toàn và báo không tìm thấy");
            }
            default -> fail("expectedResult không hợp lệ trong CSV: " + expectedResult);
        }
    }
}
