package com.example.capstone.volunteering;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VolunteerApplicationResponse {
    private Long id;
    private Long userId;
    private String userName;
    private Long opportunityId;
    private String opportunityTitle;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 