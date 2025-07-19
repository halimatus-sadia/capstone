package com.example.capstone.pet;

import com.example.capstone.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PetService {
    private final PetRepository petRepository;
    private final AuthUtils authUtils;

    public void createPetListing(PetRequestDto dto) {
        Pet pet = new Pet();
        pet.setName(dto.getName());
        pet.setSpecies(dto.getSpecies());
        pet.setBreed(dto.getBreed());
        pet.setAge(dto.getAge());
        pet.setVaccinated(dto.getVaccinated());
        pet.setDescription(dto.getDescription());
        pet.setPrice(dto.getPrice());
        pet.setLocation(dto.getLocation());
        pet.setStatus(dto.getStatus());
        pet.setOwner(authUtils.getLoggedInUser());

        petRepository.save(pet);
    }

    public List<PetResponseDto> getMyPetListings() {
        return petRepository.findAll().stream().map(p -> {
            PetResponseDto dto = new PetResponseDto();
            dto.setId(p.getId());
            dto.setName(p.getName());
            dto.setBreed(p.getBreed());
            dto.setStatus(p.getStatus().name());
            dto.setLocation(p.getLocation());
            return dto;
        }).toList();
    }
}
