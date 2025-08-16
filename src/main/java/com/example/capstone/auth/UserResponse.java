package com.example.capstone.auth;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserResponse {
    private Long id;
    private String name;
    private String username;
    private String email;
    private String profileImageFilePath;

    // for UI only
    private String newPassword;
    private String confirmPassword;
    private MultipartFile image;
}
