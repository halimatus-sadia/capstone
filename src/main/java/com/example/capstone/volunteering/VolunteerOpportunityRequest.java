package com.example.capstone.volunteering;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class VolunteerOpportunityRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Type is required")
    private VolunteerOpportunityType type;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotBlank(message = "Description is required")
    @Size(max = 2000, message = "Description is too long")
    private String description;

    @NotNull(message = "Status is required")
    private VolunteerOpportunityStatus status;

    @Positive(message = "Max volunteers must be positive")
    private Integer maxVolunteers; // Optional; leave null for unlimited

    private String requirements;   // Optional
    private String contactInfo;    // Optional

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public VolunteerOpportunityType getType() { return type; }
    public void setType(VolunteerOpportunityType type) { this.type = type; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public VolunteerOpportunityStatus getStatus() { return status; }
    public void setStatus(VolunteerOpportunityStatus status) { this.status = status; }

    public Integer getMaxVolunteers() { return maxVolunteers; }
    public void setMaxVolunteers(Integer maxVolunteers) { this.maxVolunteers = maxVolunteers; }

    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }

    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
}
