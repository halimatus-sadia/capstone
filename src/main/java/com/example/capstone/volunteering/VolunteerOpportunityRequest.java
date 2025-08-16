package com.example.capstone.volunteering;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class VolunteerOpportunityRequest {
    @NotBlank
    @Size(max = 100)
    private String title;

    @NotBlank
    @Size(max = 2000)
    private String description;

    @NotBlank
    @Size(max = 100)
    private String location;

    @NotBlank
    @Size(max = 50)
    private String type; // rescue, foster, event help, etc.

    @NotNull
    private LocalDate date;

    @NotBlank
    @Size(max = 20)
    private String status; // open, closed, etc.
} 