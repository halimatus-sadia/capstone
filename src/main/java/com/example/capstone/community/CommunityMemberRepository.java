// src/main/java/com/example/capstone/community/CommunityMemberRepository.java
package com.example.capstone.community;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityMemberRepository extends JpaRepository<CommunityMember, Long> {
    Optional<CommunityMember> findByCommunityIdAndUserId(Long communityId, Long userId);

    long countByCommunityId(Long communityId);

    boolean existsByCommunityIdAndUserId(Long communityId, Long userId);

    void deleteByCommunityIdAndUserId(Long communityId, Long userId);

    @Query("select m.user.id from CommunityMember m where m.community.id = :communityId")
    List<Long> findUserIdsByCommunityId(Long communityId);
}
