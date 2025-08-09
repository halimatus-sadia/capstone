package com.example.capstone.pet.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetRequestRepository extends JpaRepository<PetRequest, Long> {
    boolean existsByPetIdAndStatusIn(Long petId, List<PetRequestStatus> statuses);

    // Sent = requests I made (I'm the requester)
    // Eagerly load pet to avoid N+1
    @EntityGraph(attributePaths = {"pet"})
    @Query(value = """
            select petReq from PetRequest petReq
            where petReq.requestedBy.id = :requestedById
            and (:status is null or petReq.status = :status)
            """)
    Page<PetRequest> findAllByRequestedById(Long requestedById, PetRequestStatus status, Pageable pageable);

    @Query(value = """
            select petReq from PetRequest petReq
            where petReq.pet.owner.id = :me
            and (:status is null or petReq.status = :status)
            """)
    Page<PetRequest> findByPetOwnerIdAndStatus(Long me, PetRequestStatus status, Pageable pageable);

    Optional<PetRequest> findFirstByPetIdAndStatusInOrderByCreatedAtDesc(Long petId, List<PetRequestStatus> statuses);
}