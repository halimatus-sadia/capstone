package com.example.capstone.utils;

import org.springframework.web.multipart.MultipartFile;

public class MultipartUtils {
    private MultipartUtils() {

    }

    public static boolean isEmpty(MultipartFile file) {
        return file == null || file.isEmpty();
    }
}
