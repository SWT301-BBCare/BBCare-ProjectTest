package com.bbcare.automation.base;

import com.bbcare.automation.utils.ConfigReader;
import com.bbcare.automation.utils.ScreenshotOnFailure;
import com.example.demo.DemoApplication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 * Class cơ sở cho mọi Test class trong dự án.
 * TỰ ĐỘNG BẬT SERVER NẾU CHƯA CHẠY: Bạn chỉ cần ấn Run Test là server tự bật và chạy test!
 * Tự động khởi tạo ChromeDriver, đọc BASE_URL và chụp màn hình khi Fail.
 */
public abstract class BaseTest {
    protected WebDriver driver;
    protected static final String BASE_URL = ConfigReader.get("base.url");
    private static ConfigurableApplicationContext springAppContext;

    @BeforeAll
    public static synchronized void ensureServerStarted() {
        if (!isServerRunning(BASE_URL)) {
            System.out.println(">>> [AUTO-START] Chưa phát hiện server BBCare đang chạy. Đang tự động khởi động server ngầm...");
            springAppContext = SpringApplication.run(DemoApplication.class);
            // Đợi server sẵn sàng nhận request
            long startTime = System.currentTimeMillis();
            while (!isServerRunning(BASE_URL) && (System.currentTimeMillis() - startTime < 30000)) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {}
            }
            System.out.println(">>> [AUTO-START] Server BBCare đã sẵn sàng phục vụ kiểm thử!");
        } else {
            System.out.println(">>> [SERVER-CHECK] Server BBCare đã chạy sẵn tại: " + BASE_URL);
        }
    }

    private static boolean isServerRunning(String urlStr) {
        try {
            URL u = URI.create(urlStr).toURL();
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setConnectTimeout(1500);
            conn.setReadTimeout(1500);
            conn.setRequestMethod("HEAD");
            int code = conn.getResponseCode();
            return code > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @RegisterExtension
    ScreenshotOnFailure screenshot = new ScreenshotOnFailure(() -> driver);

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        if (Boolean.parseBoolean(ConfigReader.get("headless"))) {
            options.addArguments("--headless=new");
        }
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        options.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.ACCEPT);
        options.addArguments("--start-maximized");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(options);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            String raw = ConfigReader.get("end.test.delay.ms");
            if (raw != null && !raw.isBlank()) {
                long ms = Long.parseLong(raw);
                if (ms > 0) {
                    long end = System.currentTimeMillis() + ms;
                    new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofMillis(ms + 5000))
                            .pollingEvery(java.time.Duration.ofMillis(100))
                            .until(d -> System.currentTimeMillis() >= end);
                }
            }
            driver.quit();
        }
    }
}
