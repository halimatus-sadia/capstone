package com.example.capstone.volunteering;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpportunityRepo extends JpaRepository<VolunteerOpportunity, Long> {
} 
