package com.example.capstone.pet;

import com.example.capstone.pet.chat.PetSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long>, JpaSpecificationExecutor<Pet> {

    @Query(value = """
            SELECT p FROM Pet p
            WHERE (:species IS NULL OR LOWER(p.species) LIKE LOWER(CONCAT('%', :species, '%')))
              AND (:breed   IS NULL OR LOWER(p.breed)   LIKE LOWER(CONCAT('%', :breed, '%')))
              AND (:status  IS NULL OR p.status = :status)
              AND (:location IS NULL OR LOWER(p.location) LIKE LOWER(CONCAT('%', :location, '%')))
              AND COALESCE(p.isRequestAccepted, FALSE) = FALSE
              AND (:ownPets IS FALSE AND p.owner.id != :userId OR :ownPets IS TRUE AND p.owner.id = :userId)
            """)
    Page<Pet> findAllPets(
            @Param("species") String species,
            @Param("breed") String breed,
            @Param("status") PetStatus status,
            @Param("location") String location,
            @Param("userId") Long userId,
            @Param("ownPets") boolean ownPets,
            Pageable pageable);

    List<Pet> findAllByOwnerId(Long ownerId);

    @Query("select new com.example.capstone.pet.chat.PetSummary(p.id, p.name, '') from Pet p where p.id=:id")
    Optional<PetSummary> findSummaryById(Long id);

    List<Pet> findTop6ByOrderByCreatedAtDesc();
}
