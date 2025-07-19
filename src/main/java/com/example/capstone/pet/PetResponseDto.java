package com.example.capstone.pet;

import lombok.Data;

@Data
public class PetResponseDto {
    private Long id;
    private String name;
    private String breed;
    private String status;
    private String location;
    private Double price;
    // constructor or builder
}
