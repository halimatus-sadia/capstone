package com.example.capstone.volunteering;

import org.springframework.stereotype.Component;

@Component
public class VolunteerMapper {

    public VolunteerOpportunityResponse toResponse(VolunteerOpportunity o) {
        VolunteerOpportunityResponse r = new VolunteerOpportunityResponse();
        r.setId(o.getId());
        r.setTitle(o.getTitle());
        r.setDescription(o.getDescription());
        r.setLocation(o.getLocation());
        r.setStartDate(o.getStartDate());
        r.setEndDate(o.getEndDate());
        r.setStatus(o.getStatus());
        r.setMaxVolunteers(o.getMaxVolunteers());
        r.setCreatedByUsername(o.getCreatedBy() != null ? o.getCreatedBy().getUsername() : null);
        r.setCreatedAt(o.getCreatedAt());
        r.setUpdatedAt(o.getUpdatedAt());
        return r;
    }

    public VolunteerApplicationResponse toResponse(VolunteerApplication a) {
        VolunteerApplicationResponse r = new VolunteerApplicationResponse();
        r.setId(a.getId());
        r.setOpportunityId(a.getOpportunity().getId());
        r.setOpportunityTitle(a.getOpportunity().getTitle());
        r.setApplicantUsername(a.getApplicant() != null ? a.getApplicant().getUsername() : null);
        r.setMotivation(a.getMotivation());
        r.setStatus(a.getStatus());
        r.setCreatedAt(a.getCreatedAt());
        r.setUpdatedAt(a.getUpdatedAt());
        return r;
    }
}
