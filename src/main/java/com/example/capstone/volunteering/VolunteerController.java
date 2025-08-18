package com.example.capstone.volunteering;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/volunteering")
public class VolunteerController {

	private final VolunteeringService volunteering;

	@Autowired
	public VolunteerController(VolunteeringService volunteering) {
		this.volunteering = volunteering;
	}

	// LIST + FILTER + PAGINATION
	@GetMapping
	public String list(
			@RequestParam Optional<String> location,
			@RequestParam Optional<VolunteerOpportunityType> type,
			@RequestParam Optional<LocalDate> date,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "12") int size,
			Model model
	) {
		Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1),
				Sort.by(Sort.Direction.DESC, "startDate"));
		Page<VolunteerOpportunityResponse> result =
				volunteering.findAll(location.orElse(null), type.orElse(null), date.orElse(null), pageable);
		model.addAttribute("page", result);
		return "volunteering/index";
	}

	// DETAIL
	@GetMapping("/{id}")
	public String detail(@PathVariable Long id, Model model, Principal principal) {
		VolunteerOpportunityResponse o = volunteering.getById(id);
		boolean isOwner = principal != null && volunteering.isOwner(id, principal.getName());
		boolean isAdmin = principal != null && volunteering.isAdmin(principal.getName());
		boolean alreadyApplied = principal != null && volunteering.alreadyApplied(id, principal.getName());

		model.addAttribute("opportunity", o);
		model.addAttribute("isOwner", isOwner);
		model.addAttribute("isAdmin", isAdmin);
		model.addAttribute("alreadyApplied", alreadyApplied);
		return "volunteering/detail";
	}

	// CREATE (GET)
	@GetMapping("/create")
	public String createForm(Model model) {
		if (!model.containsAttribute("form")) {
			model.addAttribute("form", new VolunteerOpportunityRequest());
		}
		return "volunteering/create";
	}

	// CREATE (POST)
	@PostMapping("/create")
	public String create(
			@Valid VolunteerOpportunityRequest form,
			BindingResult errors,
			RedirectAttributes ra,
			Principal principal
	) {
		if (form.getStartDate() != null && form.getEndDate() != null
				&& form.getEndDate().isBefore(form.getStartDate())) {
			errors.rejectValue("endDate", "invalid", "End date cannot be before start date");
		}

		if (errors.hasErrors()) {
			ra.addFlashAttribute("org.springframework.validation.BindingResult.form", errors);
			ra.addFlashAttribute("form", form);
			return "redirect:/volunteering/create";
		}

		String username = principal != null ? principal.getName() : null;
		volunteering.create(form, username);
		ra.addFlashAttribute("success", true);
		return "redirect:/volunteering";
	}

	// APPLY (simple redirect)
	@PostMapping("/{id}/apply")
	public String apply(@PathVariable Long id, Principal principal, RedirectAttributes ra) {
		String username = principal != null ? principal.getName() : null;
		volunteering.apply(id, username);
		ra.addFlashAttribute("success", "Application submitted!");
		return "redirect:/volunteering/" + id;
	}

	// DELETE (owner/admin)
	@PostMapping("/{id}")
	public String delete(@PathVariable Long id, Principal principal, RedirectAttributes ra) {
		String username = principal != null ? principal.getName() : null;
		volunteering.delete(id, username);
		ra.addFlashAttribute("success", "Opportunity deleted");
		return "redirect:/volunteering";
	}

	// MY APPLICATIONS
	@GetMapping("/applications")
	public String myApplications(Model model, Principal principal,
								 @RequestParam Optional<String> success,
								 @RequestParam Optional<String> error) {
		String username = principal != null ? principal.getName() : null;
		model.addAttribute("applications", volunteering.getMyApplications(username));
		success.ifPresent(s -> model.addAttribute("success", s));
		error.ifPresent(e -> model.addAttribute("error", e));
		return "volunteering/my-applications";
	}

	// WITHDRAW
	@PostMapping("/applications/{appId}/withdraw")
	public String withdraw(@PathVariable Long appId, Principal principal, RedirectAttributes ra) {
		String username = principal != null ? principal.getName() : null;
		volunteering.withdraw(appId, username);
		ra.addFlashAttribute("success", "Application withdrawn successfully!");
		return "redirect:/volunteering/applications";
	}
}
