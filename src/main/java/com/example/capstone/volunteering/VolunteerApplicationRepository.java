package com.example.capstone.volunteering;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VolunteerApplicationRepository extends JpaRepository<VolunteerApplication, Long> {
    
    List<VolunteerApplication> findByUserId(Long userId);
    
    List<VolunteerApplication> findByOpportunityId(Long opportunityId);
    
    List<VolunteerApplication> findByStatus(String status);
    
    Optional<VolunteerApplication> findByUserIdAndOpportunityId(Long userId, Long opportunityId);
    
    @Query("SELECT va FROM VolunteerApplication va WHERE va.user.id = :userId AND va.status = :status")
    List<VolunteerApplication> findByUserIdAndStatus(Long userId, String status);
    
    boolean existsByUserIdAndOpportunityId(Long userId, Long opportunityId);
}
