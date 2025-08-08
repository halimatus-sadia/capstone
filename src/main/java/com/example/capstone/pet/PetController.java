package com.example.capstone.pet;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    public String createPetListing(
            @Valid @ModelAttribute("pet") PetRequestDto dto,
            BindingResult bindingResult,
            Model model) {
        model.addAttribute("statuses", PetStatus.values());
        if (bindingResult.hasErrors()) {
            // Redisplay the form with errors
            return "pet/create";
        }
        petService.createPetListing(dto);
        return "redirect:/pets";
    }

    @GetMapping
    public String listPets(
            @RequestParam(required = false) String species,
            @RequestParam(required = false) String breed,
            @RequestParam(required = false) PetStatus status,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "0", required = false) int page,     // 0-based
            @RequestParam(defaultValue = "12", required = false) int size,
            @RequestParam(defaultValue = "NEWEST", required = false) String sort,
            Model model) {
        Page<PetResponseDto> pets = petService.getFilteredPets(
                species,
                breed,
                status,
                location,
                page,
                size,
                sort);

        // range text like "13–24 of 86"
        int rangeStart = pets.getTotalElements() == 0 ? 0 : pets.getNumber() * pets.getSize() + 1;
        int rangeEnd = Math.min((pets.getNumber() + 1) * pets.getSize(), (int) pets.getTotalElements());

        model.addAttribute("pets", pets);
        model.addAttribute("species", species);
        model.addAttribute("breed", breed);
        model.addAttribute("status", status);
        model.addAttribute("location", location);
        model.addAttribute("statuses", PetStatus.values());

        model.addAttribute("size", size);
        model.addAttribute("sort", sort);
        model.addAttribute("rangeStart", rangeStart);
        model.addAttribute("rangeEnd", rangeEnd);
        model.addAttribute("pageNumbers", computePageNumbers(pets));

        return "pet/list";
    }

    @GetMapping("/{id}")
    public String viewPet(@PathVariable Long id, Model model) {
        model.addAttribute("pet", petService.getById(id));
        return "pet/detail";
    }

    // region private-methods
    // Build a compact pager with ellipses. Returns a List<Object> of Integer and "…" (String).
    private List<Object> computePageNumbers(Page<?> page) {
        int total = page.getTotalPages();
        int current = page.getNumber();
        int maxButtons = 7;

        List<Object> out = new ArrayList<>();
        if (total <= 1) return out; // nothing to render

        if (total <= maxButtons) {
            for (int i = 0; i < total; i++) out.add(i);
            return out;
        }

        // Always show first & last, +/-1 around current, with gaps
        out.add(0);

        int left = Math.max(1, current - 1);
        int right = Math.min(total - 2, current + 1);

        if (left > 1) out.add("…");
        for (int i = left; i <= right; i++) out.add(i);
        if (right < total - 2) out.add("…");

        out.add(total - 1);
        return out;
    }

    // endregion private-methods

}
