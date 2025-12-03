package com.example.capstone.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("""
            SELECT e FROM Event e
            WHERE (:keyword IS NULL OR
                   LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(e.location) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:from IS NULL OR e.startDateTime >= :from)
              AND (:to IS NULL OR e.endDateTime <= :to)
            """)
    Page<Event> search(@Param("keyword") String keyword,
                       @Param("from") LocalDateTime from,
                       @Param("to") LocalDateTime to,
                       Pageable pageable);

    @EntityGraph(attributePaths = {"goingUsers", "interestedUsers", "notifyUsers", "createdBy"})
    @Query("SELECT e FROM Event e WHERE e.id = :id")
    Optional<Event> findByIdWithParticipants(@Param("id") Long id);

    List<Event> findTop6ByStartDateTimeAfterOrderByStartDateTimeAsc(LocalDateTime after);
}
