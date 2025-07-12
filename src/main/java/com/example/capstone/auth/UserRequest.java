package com.example.capstone.auth;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserRequest {
    private String name;
    private String username;
    private String email;
    private String password;
    private MultipartFile profileImage;
}
