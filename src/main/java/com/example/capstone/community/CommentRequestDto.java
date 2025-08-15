package com.example.capstone.community;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentRequestDto {
    @NotBlank
    @Size(max = 4000)
    private String content;
    private Long parentId; // null = top-level
}
