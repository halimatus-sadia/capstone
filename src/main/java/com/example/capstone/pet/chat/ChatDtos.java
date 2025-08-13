package com.example.capstone.pet.chat;

import java.time.LocalDateTime;

public class ChatDtos {
    public record ThreadInitRequest(Long petId) {
    }

    public record ThreadInitResponse(Long threadId, Long petId, Long ownerId, Long userId) {
    }

    // now includes senderId
    public record SendMessage(Long threadId, Long senderId, String content) {
    }

    public record MessageView(Long id, Long senderId, String content, LocalDateTime sentAt) {
    }
}
