package com.example.capstone.volunteering;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Objects;

@Controller
@RequestMapping("/volunteer")
public class VolunteerController {

    private final VolunteerService service;
    private final VolunteerMapper mapper;

    public VolunteerController(VolunteerService service, VolunteerMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    // ---- Opportunities ----

    @GetMapping("/opportunities")
    public String listOpportunities(@RequestParam(value = "q", required = false) String q,
                                    @RequestParam(value = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                    @RequestParam(value = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                                    @RequestParam(value = "status", required = false) VolunteerOpportunityStatus status,
                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                    @RequestParam(value = "size", defaultValue = "12") int size,
                                    @RequestParam(value = "sort", defaultValue = "startDate,asc") String sort,
                                    Model model) {

        Sort s;
        if (sort.contains(",")) {
            String[] parts = sort.split(",", 2);
            s = "desc".equalsIgnoreCase(parts[1]) ? Sort.by(parts[0]).descending() : Sort.by(parts[0]).ascending();
        } else {
            s = Sort.by(sort).ascending();
        }
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), s);
        Page<VolunteerOpportunity> pageData = service.listOpportunities(q, from, to, status, pageable);

        model.addAttribute("opportunities", pageData.map(mapper::toResponse));
        model.addAttribute("q", q);
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        model.addAttribute("status", status);
        model.addAttribute("sort", sort);
        return "volunteer/opportunities/index";
    }

    @GetMapping("/opportunities/create")
    public String createOpportunityForm(Model model) {
        model.addAttribute("opportunity", new VolunteerOpportunityRequest());
        return "volunteer/opportunities/create";
    }

    @PostMapping("/opportunities/create")
    public String createOpportunity(@Valid @ModelAttribute("opportunity") VolunteerOpportunityRequest req,
                                    BindingResult bindingResult,
                                    Authentication auth,
                                    RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            return "volunteer/opportunities/create";
        }
        if (auth == null) {
            ra.addFlashAttribute("error", "You must be logged in.");
            return "redirect:/login?next=/volunteer/opportunities/create";
        }
        var created = service.createOpportunity(auth.getName(), req);
        ra.addFlashAttribute("success", "Opportunity created.");
        return "redirect:/volunteer/opportunities/" + created.getId();
    }

    @GetMapping("/opportunities/update/{id}")
    public String updateOpportunityForm(@PathVariable Long id, Authentication auth, Model model, RedirectAttributes ra) {
        var o = service.getOpportunity(id);
        if (auth == null || !Objects.equals(auth.getName(), o.getCreatedBy().getUsername())) {
            ra.addFlashAttribute("error", "Only the creator can edit this opportunity.");
            return "redirect:/volunteer/opportunities/" + id;
        }
        VolunteerOpportunityRequest req = new VolunteerOpportunityRequest();
        req.setTitle(o.getTitle());
        req.setDescription(o.getDescription());
        req.setLocation(o.getLocation());
        req.setStartDate(o.getStartDate());
        req.setEndDate(o.getEndDate());
        req.setMaxVolunteers(o.getMaxVolunteers());
        model.addAttribute("opportunity", req);
        model.addAttribute("opportunityId", id);
        return "volunteer/opportunities/update";
    }

    @PostMapping("/opportunities/update/{id}")
    public String updateOpportunity(@PathVariable Long id,
                                    @Valid @ModelAttribute("opportunity") VolunteerOpportunityRequest req,
                                    BindingResult bindingResult,
                                    Authentication auth,
                                    RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            return "volunteer/opportunities/update";
        }
        if (auth == null) {
            ra.addFlashAttribute("error", "You must be logged in.");
            return "redirect:/login?next=/volunteer/opportunities/update/" + id;
        }
        service.updateOpportunity(id, auth.getName(), req);
        ra.addFlashAttribute("success", "Opportunity updated.");
        return "redirect:/volunteer/opportunities/" + id;
    }

    @GetMapping("/opportunities/{id}")
    public String opportunityDetail(@PathVariable Long id, Model model) {
        var o = service.getOpportunity(id);
        model.addAttribute("opportunity", mapper.toResponse(o));
        model.addAttribute("application", new VolunteerApplicationRequest());
        return "volunteer/opportunities/detail";
    }

    @PostMapping("/opportunities/{id}/open")
    public String open(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        if (auth == null) return "redirect:/login?next=/volunteer/opportunities/" + id;
        service.openOpportunity(id, auth.getName());
        return "redirect:/volunteer/opportunities/" + id;
    }

    @PostMapping("/opportunities/{id}/close")
    public String close(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        if (auth == null) return "redirect:/login?next=/volunteer/opportunities/" + id;
        service.closeOpportunity(id, auth.getName());
        return "redirect:/volunteer/opportunities/" + id;
    }

    // ---- Applications ----

    @PostMapping("/opportunities/{id}/apply")
    public String apply(@PathVariable Long id,
                        @Valid @ModelAttribute("application") VolunteerApplicationRequest req,
                        BindingResult bindingResult,
                        Authentication auth,
                        RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            ra.addFlashAttribute("error", "Please correct the errors in your application.");
            return "redirect:/volunteer/opportunities/" + id;
        }
        if (auth == null) {
            return "redirect:/login?next=/volunteer/opportunities/" + id;
        }
        var a = service.apply(id, auth.getName(), req);
        ra.addFlashAttribute("success", "Application submitted.");
        return "redirect:/volunteer/opportunities/" + id;
    }

    @PostMapping("/applications/{id}/withdraw")
    public String withdraw(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        if (auth == null) return "redirect:/login?next=/volunteer/applications/" + id;
        service.withdraw(id, auth.getName());
        ra.addFlashAttribute("success", "Application withdrawn.");
        return "redirect:/volunteer/applications/mine";
    }

    @PostMapping("/applications/{id}/approve")
    public String approve(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        if (auth == null) return "redirect:/login";
        service.decide(id, auth.getName(), true);
        ra.addFlashAttribute("success", "Application approved.");
        return "redirect:/volunteer/opportunities";
    }

    @PostMapping("/applications/{id}/reject")
    public String reject(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        if (auth == null) return "redirect:/login";
        service.decide(id, auth.getName(), false);
        ra.addFlashAttribute("success", "Application rejected.");
        return "redirect:/volunteer/opportunities";
    }
}
