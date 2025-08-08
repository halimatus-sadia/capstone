package com.example.capstone.pet;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long>, JpaSpecificationExecutor<Pet> {

    @Query(value = """
            SELECT p FROM Pet p
            WHERE (:species IS NULL OR LOWER(p.species) LIKE LOWER(CONCAT('%', :species, '%')))
              AND (:breed   IS NULL OR LOWER(p.breed)   LIKE LOWER(CONCAT('%', :breed, '%')))
              AND (:status  IS NULL OR p.status = :status)
              AND (:location IS NULL OR LOWER(p.location) LIKE LOWER(CONCAT('%', :location, '%')))
            """)
    Page<Pet> findAllPets(
            @Param("species") String species,
            @Param("breed") String breed,
            @Param("status") PetStatus status,
            @Param("location") String location,
            Pageable pageable);
}
