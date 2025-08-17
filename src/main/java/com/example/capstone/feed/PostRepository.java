package com.example.capstone.feed;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("""
        SELECT p FROM Post p
        WHERE (:keyword IS NULL OR
               LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
               LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')))
        """)
    Page<Post> search(@Param("keyword") String keyword, Pageable pageable);

    @EntityGraph(attributePaths = {"createdBy"})
    @Query("SELECT p FROM Post p WHERE p.id = :id")
    Optional<Post> findByIdWithAuthor(@Param("id") Long id);
}
