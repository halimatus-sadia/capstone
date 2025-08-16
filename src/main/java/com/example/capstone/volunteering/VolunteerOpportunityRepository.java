package com.example.capstone.volunteering;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VolunteerOpportunityRepository extends JpaRepository<VolunteerOpportunity, Long> {
    
    List<VolunteerOpportunity> findByStatus(String status);
    
    List<VolunteerOpportunity> findByType(String type);
    
    List<VolunteerOpportunity> findByPostedById(Long postedById);
    
    @Query("SELECT vo FROM VolunteerOpportunity vo WHERE vo.status = 'open' ORDER BY vo.date ASC")
    List<VolunteerOpportunity> findOpenOpportunitiesOrderByDate();
    
    Optional<VolunteerOpportunity> findByIdAndStatus(Long id, String status);
}
