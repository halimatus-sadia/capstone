package com.example.capstone.volunteering;

import java.util.List;

public interface VolunteerService {

    List<VolunteerOpportunityResponse> getAllOpportunities();

    VolunteerOpportunityResponse createOpportunity(VolunteerOpportunityRequest request);

    VolunteerOpportunityResponse getOpportunityById(Long id);

    void applyToOpportunity(Long opportunityId);

    List<VolunteerApplicationResponse> getMyApplications();

    void decide(Long applicationId, boolean approve);
}
