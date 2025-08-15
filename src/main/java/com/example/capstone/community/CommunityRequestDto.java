package com.example.capstone.community;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommunityRequestDto {
    @NotBlank
    @Size(max = 140)
    private String name;
    @Size(max = 4096)
    private String description;
    @Size(max = 60)
    private String category;
    @Size(max = 120)
    private String location;
    @Size(max = 1024)
    private String coverImageUrl;
}
