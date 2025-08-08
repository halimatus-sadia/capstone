package com.example.capstone.pet;

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

    public Page<PetResponseDto> getFilteredPets(
            String species,
            String breed,
            PetStatus status,
            String location,
            int page,
            int size,
            String sort /*e.g. NEWEST, PRICE_ASC, PRICE_DESC, AGE_ASC, AGE_DESC, NAME_ASC, NAME_DESC*/) {
        Pageable pageable = PageRequest.of(page, size, mapSort(sort));
        return petRepository.findAllPets(
                        NullHandlerUtils.nullIfBlank(species),
                        NullHandlerUtils.nullIfBlank(breed),
                        status,
                        NullHandlerUtils.nullIfBlank(location),
                        pageable)
                .map(petMapper::toDto);
    }

    public PetResponseDto getById(Long id) {
        return petRepository.findById(id).map(petMapper::toDto).orElseThrow(() -> new RuntimeException("Pet not found"));
    }

    @Transactional(rollbackFor = Exception.class)
    public void createPetListing(PetRequestDto dto) {
        petRepository.save(petMapper.toEntity(dto));
    }

    public List<PetResponseDto> getMyPetListings() {
        return petRepository.findAll().stream().map(petMapper::toDto).toList();
    }

    private Sort mapSort(String sort) {
        String key = (sort == null || sort.isBlank()) ? "NEWEST" : sort.toUpperCase(Locale.ROOT);
        // You can add more fields later; keep defaults safe
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

}
