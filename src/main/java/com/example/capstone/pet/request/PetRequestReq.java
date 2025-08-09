package com.example.capstone.pet.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PetRequestReq {
    @NotNull(message = "Pet ID is required.")
    private Long petId;

    @NotBlank(message = "Message cannot be blank.")
    private String message;
}
