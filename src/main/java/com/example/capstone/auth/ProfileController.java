package com.example.capstone.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    @GetMapping
    public String view(Model model) {
        UserResponse profile = userService.getCurrentProfile();
        model.addAttribute("profile", profile);
        return "profile/view"; // templates/profile/view.html
    }

    @GetMapping("/edit")
    public String editForm(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", userService.getCurrentProfile());
        }
        return "profile/edit"; // templates/profile/edit.html
    }

    @PostMapping("/edit")
    public String update(
            @Valid @ModelAttribute("form") UserUpdateRequest updateRequest,
            BindingResult binding,
            RedirectAttributes ra) {

        // Bean validations first
        if (binding.hasErrors()) {
            return "profile/edit";
        }
        // Cross-field validations
        if (updateRequest.getNewPassword() != null && !updateRequest.getNewPassword().isBlank()) {
            if (!updateRequest.getNewPassword().equals(updateRequest.getConfirmPassword())) {
                binding.rejectValue("confirmPassword", "password.mismatch", "Passwords do not match");
            }
        }
        if (binding.hasErrors()) {
            return "profile/edit";
        }

        userService.updateCurrentProfile(updateRequest);
        ra.addFlashAttribute("success", "Profile updated successfully.");
        return "redirect:/profile";
    }
}