package com.example.capstone.pet.chat;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    Page<ChatMessage> findByThreadOrderBySentAtAsc(ChatThread thread, Pageable pageable);

    ChatMessage findTop1ByThreadOrderBySentAtDesc(ChatThread thread);
}