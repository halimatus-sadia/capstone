package com.example.capstone.community;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {
    @Query("SELECT p FROM CommunityPost p WHERE p.community.id = :communityId ORDER BY p.createdAt DESC")
    Page<CommunityPost> findByCommunityId(Long communityId, Pageable pageable);
}
