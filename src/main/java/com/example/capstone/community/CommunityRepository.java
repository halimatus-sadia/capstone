package com.example.capstone.community;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {

    @Query("""
              SELECT c FROM Community c
              WHERE (:q IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :q, '%')) 
                             OR LOWER(c.description) LIKE LOWER(CONCAT('%', :q, '%')))
                AND (:category IS NULL OR c.category = :category)
                AND (:location IS NULL OR LOWER(c.location) LIKE LOWER(CONCAT('%', :location, '%')))
            """)
    Page<Community> search(@Param("q") String q,
                           @Param("category") String category,
                           @Param("location") String location,
                           Pageable pageable);
}
