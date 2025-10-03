package com.example.capstone.event;

import com.example.capstone.auth.User;
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

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // List with pagination + filtering
    @GetMapping
    public String list(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(value = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "12") int size,
            @RequestParam(value = "sort", defaultValue = "startDateTime,asc") String sort,
            Model model) {

        Sort s;
        if (sort.contains(",")) {
            String[] parts = sort.split(",", 2);
            s = "desc".equalsIgnoreCase(parts[1]) ? Sort.by(parts[0]).descending() : Sort.by(parts[0]).ascending();
        } else {
            s = Sort.by(sort).ascending();
        }

        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), s);
        Page<Event> events = eventService.list(q, from, to, pageable);

        model.addAttribute("events", events);
        model.addAttribute("q", q);
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        model.addAttribute("sort", sort);
        return "events/index";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("event", new EventRequest());
        return "events/create";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("event") EventRequest req,
                         BindingResult bindingResult,
                         Authentication auth,
                         RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            return "events/create";
        }
        String username = auth != null ? auth.getName() : null;
        if (username == null) {
            ra.addFlashAttribute("error", "You must be logged in to create an event.");
            return "redirect:/login?next=/events/create";
        }
        Event created = eventService.create(username, req);
        ra.addFlashAttribute("success", "Event created.");
        return "redirect:/events/" + created.getId();
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Long id, Authentication auth, Model model, RedirectAttributes ra) {
        Event e = eventService.get(id);
        if (auth == null || !Objects.equals(auth.getName(), e.getCreatedBy().getUsername())) {
            ra.addFlashAttribute("error", "Only the creator can edit this event.");
            return "redirect:/events/" + id;
        }
        EventRequest req = new EventRequest();
        req.setTitle(e.getTitle());
        req.setDescription(e.getDescription());
        req.setLocation(e.getLocation());
        req.setStartDateTime(e.getStartDateTime());
        req.setEndDateTime(e.getEndDateTime());
        req.setImageUrl(e.getImageUrl());
        model.addAttribute("event", req);
        model.addAttribute("eventId", id);
        return "events/update";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("event") EventRequest req,
                         BindingResult bindingResult,
                         Authentication auth,
                         RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            return "events/update";
        }
        if (auth == null) {
            ra.addFlashAttribute("error", "You must be logged in.");
            return "redirect:/login?next=/events/update/" + id;
        }
        eventService.update(id, auth.getName(), req);
        ra.addFlashAttribute("success", "Event updated.");
        return "redirect:/events/" + id;
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Authentication auth, Model model) {
        Event e = eventService.get(id);
        model.addAttribute("event", e);

        String username = auth != null ? auth.getName() : null;
        boolean isCreator = username != null && username.equals(e.getCreatedBy().getUsername());
        model.addAttribute("isCreator", isCreator);

        if (username != null) {
            Optional<User> maybeUser = e.getGoingUsers().stream().filter(u -> u.getUsername().equals(username)).findFirst();
            model.addAttribute("isGoing", maybeUser.isPresent());
            boolean isInterested = e.getInterestedUsers().stream().anyMatch(u -> u.getUsername().equals(username));
            boolean isNotify = e.getNotifyUsers().stream().anyMatch(u -> u.getUsername().equals(username));
            model.addAttribute("isInterested", isInterested);
            model.addAttribute("isNotify", isNotify);
        } else {
            model.addAttribute("isGoing", false);
            model.addAttribute("isInterested", false);
            model.addAttribute("isNotify", false);
        }

        return "events/detail";
        }

    @PostMapping("/{id}/going")
    public String toggleGoing(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        if (auth == null) {
            return "redirect:/login?next=/events/" + id;
        }
        eventService.toggleGoing(id, auth.getName());
        return "redirect:/events/" + id;
    }

    @PostMapping("/{id}/interested")
    public String toggleInterested(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        if (auth == null) {
            return "redirect:/login?next=/events/" + id;
        }
        eventService.toggleInterested(id, auth.getName());
        return "redirect:/events/" + id;
    }

    @PostMapping("/{id}/notify")
    public String toggleNotify(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        if (auth == null) {
            return "redirect:/login?next=/events/" + id;
        }
        eventService.toggleNotify(id, auth.getName());
        return "redirect:/events/" + id;
    }
}
