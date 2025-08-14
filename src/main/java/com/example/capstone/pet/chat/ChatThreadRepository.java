package com.example.capstone.pet.chat;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatThreadRepository extends JpaRepository<ChatThread, Long> {
    Optional<ChatThread> findByPetIdAndOwnerIdAndUserId(Long petId, Long ownerId, Long userId);

    Page<ChatThread> findByOwnerIdOrderByCreatedAtDesc(Long ownerId, Pageable pageable);

    Page<ChatThread> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}