package com.example.capstone.pet;

import com.example.capstone.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PetService {
    private final PetMapper petMapper;
    private final PetRepository petRepository;

    public List<Pet> getFilteredPets(String species, String breed, PetStatus status, String location) {
        Specification<Pet> spec = PetSpecification.filterPets(species, breed, status, location);
        return petRepository.findAll(spec);
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
}
