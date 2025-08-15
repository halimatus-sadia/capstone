package com.example.capstone.pet;

import com.example.capstone.pet.request.HandlePetRequestDto;
import com.example.capstone.pet.request.PetRequestReq;
import com.example.capstone.pet.request.PetRequestRes;
import com.example.capstone.pet.request.PetRequestStatus;
import com.example.capstone.utils.AuthUtils;
import com.example.capstone.utils.PaginationUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/pets")
@RequiredArgsConstructor
public class PetController {
    private final PetService petService;
    private final AuthUtils authUtils;

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("pet", new PetSaveRequest());
        model.addAttribute("statuses", PetStatus.values());
        return "pet/create";
    }

    @PostMapping
    public String createPetListing(
            @Valid @ModelAttribute("pet") PetSaveRequest dto,
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
                false,
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
        model.addAttribute("pageNumbers", PaginationUtils.computePageNumbers(pets));

        return "pet/list";
    }

    @GetMapping({"/{id}"})
    public String viewOrDetailPet(@PathVariable Long id, Model model) {
        PetResponseDto pet = petService.getById(id);
        Long currentUserId = authUtils.getLoggedInUser().getId();
        boolean isOwner = currentUserId.equals(pet.getOwner().getId());

        boolean hasActiveRequest = false;
        PetRequestStatus activeReqStatus = null;
        if (!isOwner) {
            activeReqStatus = petService.getActiveRequestStatusForPet(id);
            hasActiveRequest = (activeReqStatus != null);
        }

        // Add attributes to model
        model.addAttribute("pet", pet);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("currentUserId", currentUserId);
        model.addAttribute("hasActiveRequest", hasActiveRequest);
        model.addAttribute("activeReqStatus", activeReqStatus);
        model.addAttribute("ownerId", pet.getOwner().getId());

        return "pet/detail";
    }


    @GetMapping("/own-pet-list")
    public String ownPetList(
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
                true,
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
        model.addAttribute("pageNumbers", PaginationUtils.computePageNumbers(pets));

        return "pet/own_pet_list";
    }

    @PostMapping("/requests")
    public String submitRequest(
            @ModelAttribute PetRequestReq dto,
            RedirectAttributes ra) {
        petService.createPetRequest(dto);
        ra.addFlashAttribute("success", "Request submitted successfully.");
        return "redirect:/pets/" + dto.getPetId();
    }

    @GetMapping("/requests")
    public String listRequests(
            @RequestParam(value = "status", required = false) PetRequestStatus status,
            @RequestParam(value = "view", defaultValue = "INCOMING", required = false) String view, // INCOMING/SENT
            @RequestParam(defaultValue = "0") int page,     // 0-based
            @RequestParam(defaultValue = "12") int size,
            Model model) {
        Page<PetRequestRes> reqs = petService.getRequests(status, view, page, size);
        // range text like "13–24 of 86"
        int rangeStart = reqs.getTotalElements() == 0 ? 0 : reqs.getNumber() * reqs.getSize() + 1;
        int rangeEnd = Math.min((reqs.getNumber() + 1) * reqs.getSize(), (int) reqs.getTotalElements());

        model.addAttribute("page", reqs);
        model.addAttribute("activeStatus", status);
        model.addAttribute("view", view);


        model.addAttribute("size", size);
        model.addAttribute("rangeStart", rangeStart);
        model.addAttribute("rangeEnd", rangeEnd);
        model.addAttribute("pageNumbers", PaginationUtils.computePageNumbers(reqs));

        return "pet/request/list";
    }


    @PostMapping(value = "/requests", params = "_method=put")
    public String handleRequest(
            @ModelAttribute HandlePetRequestDto dto,
            @RequestParam(value = "status", required = false) PetRequestStatus status,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            RedirectAttributes ra) {
        petService.handleRequest(dto);
        ra.addFlashAttribute("success", "Request updated.");
        // Preserve current filter/pagination context on redirect
        StringBuilder redirect = new StringBuilder("redirect:/pets/requests");
        List<String> qs = new ArrayList<>();
        if (status != null) qs.add("status=" + status);
        if (page != null) qs.add("page=" + page);
        if (size != null) qs.add("size=" + size);
        if (!qs.isEmpty()) redirect.append("?").append(String.join("&", qs));
        return redirect.toString();
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        PetSaveRequest form = petService.getEditFormForOwner(id);
        model.addAttribute("pet", form);
        model.addAttribute("petId", id);            // <-- add this
        model.addAttribute("statuses", PetStatus.values());
        return "pet/edit";
    }

    @PostMapping(value = "/{id}", params = "_method=put")
    public String updatePet(
            @PathVariable Long id,
            @Valid @ModelAttribute("pet") PetSaveRequest dto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes ra) {

        model.addAttribute("statuses", PetStatus.values());
        if (bindingResult.hasErrors()) {
            return "pet/edit";
        }

        petService.updatePetAsOwner(id, dto); // Throws if not owner
        ra.addFlashAttribute("success", "Pet updated successfully.");
        return "redirect:/pets/" + id;
    }

}
