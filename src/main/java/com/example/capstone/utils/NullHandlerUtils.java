package com.example.capstone.utils;

import org.springframework.util.StringUtils;

public class NullHandlerUtils {
    public static String nullIfBlank(String str) {
        return StringUtils.hasText(str) ? str : null;
    }
}
