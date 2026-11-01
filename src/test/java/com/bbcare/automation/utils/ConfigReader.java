package com.bbcare.automation.utils;

import java.io.InputStream;
import java.util.Properties;

/**
 * Đọc cấu hình từ config.properties.
 * Ưu tiên System.getProperty(key) (ví dụ khi chạy CI: mvn test -Dheadless=true -Dbase.url=...)
 */
public class ConfigReader {
    private static final Properties props = new Properties();

    static {
        try (InputStream is = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (is != null) {
                props.load(is);
            } else {
                throw new RuntimeException("Không tìm thấy file src/test/resources/config.properties");
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi đọc file config.properties", e);
        }
    }

    public static String get(String key) {
        String sysVal = System.getProperty(key);
        if (sysVal != null && !sysVal.isBlank()) {
            return sysVal.trim();
        }
        String val = props.getProperty(key);
        return val != null ? val.trim() : "";
    }
}

