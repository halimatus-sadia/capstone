package com.example.capstone.pet;

import com.example.capstone.auth.UserResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class PetResponseDto {
    private String name;
    private String species;
    private String breed;
    private Integer age;
    private Boolean vaccinated;
    private String description;
    private Double price;
    private String location;
    private PetStatus status;
    private LocalDateTime createdAt;
    private UserResponse owner;
}
