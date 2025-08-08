package com.example.capstone.pet;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PetSpecification {
    public static Specification<Pet> filterPets(String species, String breed, PetStatus status, String location) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (species != null && !species.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("species")), "%" + species.toLowerCase() + "%"));
            }
            if (breed != null && !breed.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("breed")), "%" + breed.toLowerCase() + "%"));
            }
            if (location != null && !location.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%"));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
