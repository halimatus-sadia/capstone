package com.example.capstone.community;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PostRequestDto {
    @Size(max = 200)
    private String title;
    @NotBlank
    @Size(max = 8000)
    private String content;
    @Size(max = 1024)
    private String imageUrl;
}
