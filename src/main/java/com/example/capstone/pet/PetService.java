package com.example.capstone.pet;

import com.example.capstone.notification.DomainEventPublisher;
import com.example.capstone.pet.request.*;
import com.example.capstone.utils.AuthUtils;
import com.example.capstone.utils.NullHandlerUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PetService {
    private final PetMapper petMapper;
    private final PetRepository petRepository;
    private final AuthUtils authUtils;
    private final PetRequestMapper petRequestMapper;
    private final PetRequestRepository petRequestRepository;
    private final DomainEventPublisher events;

    // =========================
    // Pets (existing)
    // =========================
    public Page<PetResponseDto> getFilteredPets(
            String species,
            String breed,
            PetStatus status,
            String location,
            int page,
            int size,
            boolean ownPets,
            String sort /*e.g. NEWEST, PRICE_ASC, PRICE_DESC, AGE_ASC, AGE_DESC, NAME_ASC, NAME_DESC*/) {

        Pageable pageable = PageRequest.of(page, size, mapSort(sort));
        return petRepository.findAllPets(
                        NullHandlerUtils.nullIfBlank(species),
                        NullHandlerUtils.nullIfBlank(breed),
                        status,
                        NullHandlerUtils.nullIfBlank(location),
                        authUtils.getLoggedInUser().getId(),
                        ownPets,
                        pageable)
                .map(petMapper::toDto);
    }

    public PetResponseDto getById(Long id) {
        return petRepository.findById(id)
                .map(petMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Pet not found"));
    }

    // NEW: return active request status if one exists (PENDING/ACCEPTED), else null
    public PetRequestStatus getActiveRequestStatusForPet(Long petId) {
        return petRequestRepository
                .findFirstByPetIdAndStatusInOrderByCreatedAtDesc(
                        petId, List.of(PetRequestStatus.PENDING, PetRequestStatus.ACCEPTED))
                .map(PetRequest::getStatus)
                .orElse(null);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createPetListing(PetSaveRequest dto) {
        petRepository.save(petMapper.toEntity(dto));
    }

    public List<PetResponseDto> getMyPetListings() {
        return petRepository.findAllByOwnerId(authUtils.getLoggedInUser().getId())
                .stream()
                .map(petMapper::toDto)
                .toList();
    }

    // =========================
    // Requests (new + existing)
    // =========================

    /**
     * Create a pet request. Throws if there is already a PENDING/ACCEPTED request for the same pet.
     */
    @Transactional(rollbackFor = Exception.class)
    public void createPetRequest(PetRequestReq dto) {
        Pet pet = petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new RuntimeException("Pet not found"));

        // Ensure only one active (PENDING/ACCEPTED) request per pet at a time
        if (petRequestRepository.existsByPetIdAndStatusIn(
                dto.getPetId(),
                List.of(PetRequestStatus.PENDING, PetRequestStatus.ACCEPTED))) {
            throw new RuntimeException("A request for this pet already PENDING/ACCEPTED.");
        }

        PetRequest saved = petRequestRepository.save(petRequestMapper.toEntity(dto, pet));

        // Example inside PetRequestService after save
        events.petRequestCreated(
                /* senderUserId    */ authUtils.getLoggedInUser().getId(),        // who performed the action
                /* recipientUserId */ saved.getPet().getOwner().getId(),           // who should be notified
                /* petRequestId    */ saved.getId(),
                /* summary         */ "New request for " + saved.getPet().getName(),
                /* link            */ "/pets/requests"
        );


    }

    /**
     * Handle a request (ACCEPT / REJECT). PENDING is not allowed.
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleRequest(HandlePetRequestDto dto) {
        PetRequest request = petRequestRepository
                .findById(dto.getPetRequestId())
                .orElseThrow(() -> new RuntimeException("Pet request not found."));
        Pet pet = request.getPet();
        if (dto.getStatus() == PetRequestStatus.PENDING) {
            throw new RuntimeException("Request can't be PENDING.");
        }
        if (request.getStatus() != PetRequestStatus.PENDING) {
            throw new RuntimeException("Request is already ACCEPTED/REJECTED.");
        }

        if (dto.getStatus() == PetRequestStatus.ACCEPTED) {
            pet.setIsRequestAccepted(true);
            petRepository.save(pet);
        }
        request.setStatus(dto.getStatus());
        petRequestRepository.save(request);
    }

    /**
     * Paginated & filterable list of requests for the admin/owner view.
     * - Filters by status when provided (PENDING/ACCEPTED/REJECTED); otherwise returns all.
     * - Sorted by createdAt DESC, id DESC for stable pagination.
     */
    public Page<PetRequestRes> getRequests(PetRequestStatus status, String view, int page, int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id")));

        Long me = authUtils.getLoggedInUser().getId();
        boolean sent = "SENT".equalsIgnoreCase(view);

        Page<PetRequest> result;
        if (sent) {
            result = petRequestRepository.findAllByRequestedById(me, status, pageable);
        } else {
            // default to INCOMING
            result = petRequestRepository.findByPetOwnerIdAndStatus(me, status, pageable);
        }
        return result.map(petRequestMapper::toDto);
    }

    public PetSaveRequest getEditFormForOwner(Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found"));
        ensureOwnerOrThrow(pet);
        return petMapper.toSaveRequest(pet); // prefill fields
    }

    @Transactional(rollbackFor = Exception.class)
    public void updatePetAsOwner(Long petId, PetSaveRequest dto) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found"));
        ensureOwnerOrThrow(pet);

        petMapper.updatePet(dto, pet);
        petRepository.save(pet);
    }

    // region private-methods

    private Sort mapSort(String sort) {
        String key = (sort == null || sort.isBlank()) ? "NEWEST" : sort.toUpperCase(Locale.ROOT);
        // Keep defaults safe and nulls last to avoid JPA null precedence issues
        return switch (key) {
            case "PRICE_ASC" -> Sort.by(Sort.Order.asc("price").nullsLast());
            case "PRICE_DESC" -> Sort.by(Sort.Order.desc("price").nullsLast());
            case "AGE_ASC" -> Sort.by(Sort.Order.asc("age").nullsLast());
            case "AGE_DESC" -> Sort.by(Sort.Order.desc("age").nullsLast());
            case "NAME_ASC" -> Sort.by(Sort.Order.asc("name").nullsLast());
            case "NAME_DESC" -> Sort.by(Sort.Order.desc("name").nullsLast());
            case "NEWEST", "RECENT" -> Sort.by(Sort.Order.desc("id")); // default
            default -> Sort.by(Sort.Order.desc("id"));
        };
    }

    private void ensureOwnerOrThrow(Pet pet) {
        Long me = authUtils.getLoggedInUser().getId();
        if (pet.getOwner() == null || !pet.getOwner().getId().equals(me)) {
            // Use Spring Security's exception if you have it on classpath; otherwise RuntimeException.
            throw new org.springframework.security.access.AccessDeniedException("Only the owner can edit this pet");
        }
    }

    // endregion private-methods
}
