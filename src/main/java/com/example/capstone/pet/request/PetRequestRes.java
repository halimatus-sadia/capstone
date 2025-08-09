package com.example.capstone.pet.request;

import com.example.capstone.pet.PetResponseDto;

import java.time.LocalDateTime;

public record PetRequestRes(
        Long id,
        String message,
        PetRequestStatus status,
        LocalDateTime createdAt,
        PetResponseDto pet,
        String requesterName,
        String requesterContact) {
}