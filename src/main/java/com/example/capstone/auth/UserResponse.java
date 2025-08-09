package com.example.capstone.auth;

import lombok.Data;

import java.util.Set;

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
}
