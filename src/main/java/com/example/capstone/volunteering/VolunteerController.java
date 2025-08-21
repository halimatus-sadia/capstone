package com.example.capstone.volunteering;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/volunteer")
public class VolunteerController {

    private final VolunteerService service;

    public VolunteerController(VolunteerService service) {
        this.service = service;
    }

    @GetMapping("/opportunities")
    public String list(Model model) {
        List<VolunteerOpportunityResponse> all = service.getAllOpportunities();
        model.addAttribute("opportunities", all);
        return "volunteering/index";
    }

    @GetMapping("/opportunities/create")
    public String createForm(Model model) {
        model.addAttribute("opportunity", new VolunteerOpportunityRequest());
        return "volunteering/create";
    }

    @PostMapping("/opportunities")
    public String create(@Valid @ModelAttribute("opportunity") VolunteerOpportunityRequest req,
                         BindingResult bindingResult,
                         Authentication auth,
                         RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            return "volunteering/create";
        }
        if (auth == null) {
            ra.addFlashAttribute("error", "You must be logged in.");
            return "redirect:/login?next=/volunteer/opportunities/create";
        }
        service.createOpportunity(req);
        ra.addFlashAttribute("success", "Opportunity created.");
        return "redirect:/volunteer/opportunities";
    }

    @PostMapping("/opportunities/{id}/apply")
    public String apply(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        if (auth == null) return "redirect:/login";
        service.applyToOpportunity(id);
        ra.addFlashAttribute("success", "Applied successfully.");
        return "redirect:/volunteer/opportunities";
    }

    @PostMapping("/applications/{id}/approve")
    public String approve(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        if (auth == null) return "redirect:/login";
        service.decide(id, true);
        ra.addFlashAttribute("success", "Application approved.");
        return "redirect:/volunteer/opportunities";
    }

    @PostMapping("/applications/{id}/reject")
    public String reject(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        if (auth == null) return "redirect:/login";
        service.decide(id, false);
        ra.addFlashAttribute("success", "Application rejected.");
        return "redirect:/volunteer/opportunities";
    }
}
