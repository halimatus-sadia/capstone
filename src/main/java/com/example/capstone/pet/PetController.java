package com.example.capstone.pet;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/pets")
@RequiredArgsConstructor
public class PetController {
    private final PetService petService;

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("pet", new PetRequestDto());
        model.addAttribute("statuses", PetStatus.values());
        return "pet/create";
    }

    @PostMapping
    public String createPetListing(@ModelAttribute("pet") PetRequestDto dto) {
        petService.createPetListing(dto);
        return "redirect:/pets";
    }

    @GetMapping
    public String showAllPets(Model model) {
        List<PetResponseDto> pets = petService.getMyPetListings();
        model.addAttribute("pets", pets);
        return "pet/list";
    }
}
