package com.example.capstone.volunteering;

import com.example.capstone.auth.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VolunteerApplicationRepository extends JpaRepository<VolunteerApplication, Long> {

    boolean existsByApplicantAndOpportunity(User applicant, VolunteerOpportunity opportunity);

    Optional<VolunteerApplication> findByApplicantAndOpportunity(User applicant, VolunteerOpportunity opportunity);

    long countByOpportunityAndStatus(VolunteerOpportunity opportunity, VolunteerApplicationStatus status);

    @EntityGraph(attributePaths = {"opportunity", "applicant"})
    Page<VolunteerApplication> findByApplicant(User applicant, Pageable pageable);
}