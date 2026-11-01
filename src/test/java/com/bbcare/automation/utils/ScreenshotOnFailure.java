package com.bbcare.automation.utils;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

/**
 * JUnit 5 Extension: Tự động chụp ảnh màn hình lưu vào target/screenshots/
 * ngay khi một Test Case bị Fail (chạy trước @AfterEach tearDown).
 */
public class ScreenshotOnFailure implements AfterTestExecutionCallback {
    private final Supplier<WebDriver> driverSupplier;

    public ScreenshotOnFailure(Supplier<WebDriver> driverSupplier) {
        this.driverSupplier = driverSupplier;
    }

    @Override
    public void afterTestExecution(ExtensionContext ctx) throws Exception {
        if (ctx.getExecutionException().isPresent() && driverSupplier.get() != null) {
            byte[] png = ((TakesScreenshot) driverSupplier.get()).getScreenshotAs(OutputType.BYTES);
            Path dir = Paths.get("target", "screenshots");
            Files.createDirectories(dir);
            String name = ctx.getDisplayName().replaceAll("[^a-zA-Z0-9-_]", "_") + ".png";
            Files.write(dir.resolve(name), png);
        }
    }
}

