package com.example.capstone.volunteering;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface VolunteeringService {

    // Listing + filters (filters may be ignored if repository doesn't support them yet)
    Page<VolunteerOpportunityResponse> findAll(String location, VolunteerOpportunityType type, LocalDate date, Pageable pageable);

    // Details
    VolunteerOpportunityResponse getById(Long id);

    // Create / Delete
    void create(VolunteerOpportunityRequest form, String username);
    void delete(Long id, String username);

    // Apply / Withdraw
    void apply(Long opportunityId, String username);
    boolean alreadyApplied(Long opportunityId, String username);
    void withdraw(Long applicationId, String username);

    // Ownership / Admin check
    boolean isOwner(Long opportunityId, String username);
    boolean isAdmin(String username);

    // My applications
    List<VolunteerApplicationResponse> getMyApplications(String username);
}
