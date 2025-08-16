package com.example.capstone.volunteering;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VolunteerApplicationRequest {
    @NotNull
    private Long opportunityId;

    @NotNull
    private String status; // applied, accepted, completed
} 