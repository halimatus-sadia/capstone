package com.example.capstone.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserUpdateRequest {
    @NotNull
    private Long id;

    @NotBlank
    @Size(max = 60)
    private String name;

    @NotBlank
    @Size(min = 3, max = 30)
    private String username;

    @NotBlank
    @Email
    private String email;

    // Optional password change
    @Size(max = 100)
    private String newPassword;

    @Size(max = 100)
    private String confirmPassword;

    // TODO: handle file upload from UI and save it to MinIO
    private MultipartFile image;
}