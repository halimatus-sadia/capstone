package com.example.capstone.pet;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PetRequestDto {
    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name cannot be longer than 50 characters")
    private String name;

    @NotBlank(message = "Species is required")
    @Size(max = 30, message = "Species cannot be longer than 30 characters")
    private String species;

    @Size(max = 30, message = "Breed cannot be longer than 30 characters")
    private String breed;

    @NotNull(message = "Age is required")
    @Min(value = 0, message = "Age cannot be negative")
    @Max(value = 50, message = "Age seems too high")
    private Integer age;

    private Boolean vaccinated = false;

    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Invalid price format")
    private Double price;

    @NotBlank(message = "Location is required")
    @Size(max = 100, message = "Location cannot be longer than 100 characters")
    private String location;

    @NotNull(message = "Status is required")
    private PetStatus status;
}
