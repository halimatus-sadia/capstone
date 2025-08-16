package com.example.capstone.volunteering;

import lombok.Data;

import java.time.LocalDate;

@Data
public class VolunteerOpportunityResponse {
    private Long id;
    private String title;
    private String description;
    private String location;
    private String type;
    private LocalDate date;
    private String status;
    private Long postedById;
    private String postedByName;
} 