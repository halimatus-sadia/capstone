package com.example.capstone.volunteering;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OpportunityService {
    @Autowired
    private OpportunityRepo opportunityRepository;

    public List<VolunteerOpportunity> getAllOpportunities() {
        return opportunityRepository.findAll();
    }

    public Optional<VolunteerOpportunity> getOpportunityById(Long id) {
        return opportunityRepository.findById(id);
    }

    public VolunteerOpportunity createOpportunity(VolunteerOpportunity opportunity) {
        return opportunityRepository.save(opportunity);
    }

    public VolunteerOpportunity updateOpportunity(Long id, VolunteerOpportunity details) {
        return opportunityRepository.findById(id).map(opportunity -> {
            opportunity.setTitle(details.getTitle());
            opportunity.setDescription(details.getDescription());
            opportunity.setLocation(details.getLocation());
            opportunity.setType(details.getType());
            opportunity.setDate(details.getDate());
            opportunity.setStatus(details.getStatus());
            opportunity.setPostedBy(details.getPostedBy());
            return opportunityRepository.save(opportunity);
        }).orElseGet(() -> {
            details.setId(id);
            return opportunityRepository.save(details);
        });
    }

    public void deleteOpportunity(Long id) {
        opportunityRepository.deleteById(id);
    }
} 
