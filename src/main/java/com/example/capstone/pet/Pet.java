package com.example.capstone.pet;

import com.example.capstone.auth.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "pet")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String species;
    private String breed;
    private Integer age;
    private Boolean vaccinated;
    private String description;
    private Double price;

    @Enumerated(EnumType.STRING)
    private PetStatus status; // FOR_SALE, FOR_RENT, FOR_ADOPTION

    private String location;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    private LocalDateTime createdAt = LocalDateTime.now();

}
