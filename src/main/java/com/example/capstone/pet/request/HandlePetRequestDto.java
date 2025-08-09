package com.example.capstone.pet.request;

import lombok.Data;

@Data
public class HandlePetRequestDto {
    private Long petRequestId;
    private PetRequestStatus status;
}
