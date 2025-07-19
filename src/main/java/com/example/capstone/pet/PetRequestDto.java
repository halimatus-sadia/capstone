package com.example.capstone.pet;

import lombok.Data;

@Data
public class PetRequestDto {
    private String name;
    private String species;
    private String breed;
    private Integer age;
    private Boolean vaccinated;
    private String description;
    private Double price;
    private String location;
    private PetStatus status;
}
