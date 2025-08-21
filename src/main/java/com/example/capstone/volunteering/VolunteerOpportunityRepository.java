package com.example.capstone.volunteering;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface VolunteerOpportunityRepository extends JpaRepository<VolunteerOpportunity, Long> {

    @Query("""
        SELECT o FROM VolunteerOpportunity o
        WHERE (:keyword IS NULL OR
               LOWER(o.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
               LOWER(o.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
               LOWER(o.location) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:from IS NULL OR o.startDate >= :from)
          AND (:to IS NULL OR o.endDate <= :to)
          AND (:status IS NULL OR o.status = :status)
        """)
    Page<VolunteerOpportunity> search(@Param("keyword") String keyword,
                                      @Param("from") LocalDate from,
                                      @Param("to") LocalDate to,
                                      @Param("status") VolunteerOpportunityStatus status,
                                      Pageable pageable);

    @EntityGraph(attributePaths = {"createdBy"})
    @Query("SELECT o FROM VolunteerOpportunity o WHERE o.id = :id")
    Optional<VolunteerOpportunity> findByIdWithCreator(@Param("id") Long id);
}
