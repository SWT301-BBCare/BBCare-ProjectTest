package com.bbcare.automation.utils;

/**
 * Hỗ trợ xử lý dữ liệu động trong file CSV (ví dụ thay thế {RANDOM} bằng số ngẫu nhiên theo thời gian)
 * để tránh trùng lặp Email/SĐT khi chạy kiểm thử Đăng ký nhiều lần.
 */
public class DataGenerator {

    /**
     * Thay thế chuỗi "{RANDOM}" trong dữ liệu đầu vào bằng chuỗi số ngẫu nhiên 6 chữ số.
     */
    public static String resolve(String rawValue) {
        if (rawValue == null) {
            return null;
        }
        String suffix = String.valueOf(System.currentTimeMillis() % 1000000);
        return rawValue.replace("{RANDOM}", suffix);
    }
}

