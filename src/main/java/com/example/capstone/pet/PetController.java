package com.example.capstone.pet;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/pets")
@RequiredArgsConstructor
public class PetController {
    private final PetService petService;

    @GetMapping("/new")
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
    public String listPets(
            @RequestParam(required = false) String species,
            @RequestParam(required = false) String breed,
            @RequestParam(required = false) PetStatus status,
            @RequestParam(required = false) String location,
            Model model) {
        List<Pet> pets = petService.getFilteredPets(species, breed, status, location);
        model.addAttribute("pets", pets);
        model.addAttribute("species", species);
        model.addAttribute("breed", breed);
        model.addAttribute("status", status);
        model.addAttribute("location", location);
        model.addAttribute("statuses", PetStatus.values());
        return "pet/list";
    }

    @GetMapping("/{id}")
    public String viewPet(@PathVariable Long id, Model model) {
        model.addAttribute("pet", petService.getById(id));
        return "pet/detail";
    }
}
