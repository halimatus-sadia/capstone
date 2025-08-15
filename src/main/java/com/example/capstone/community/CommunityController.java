package com.example.capstone.community;

import com.example.capstone.utils.AuthUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/communities")
@RequiredArgsConstructor
public class CommunityController {
    private final CommunityService communityService;
    private final CommunityPostService communityPostService;
    private final AuthUtils auth;

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(required = false) String category,
                       @RequestParam(required = false) String location,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "12") int size,
                       @RequestParam(defaultValue = "createdAt,desc") String sort,
                       Model model) {

        Sort s = Sort.by(Sort.Order.desc("createdAt"));
        if (sort != null && !sort.isBlank()) s = Sort.by(sort.split(",")[1].equalsIgnoreCase("asc")
                ? Sort.Order.asc(sort.split(",")[0]) : Sort.Order.desc(sort.split(",")[0]));
        Page<Community> result = communityService.browse(q, category, location, PageRequest.of(page, size, s));

        model.addAttribute("communities", result);
        model.addAttribute("q", q);
        model.addAttribute("category", category);
        model.addAttribute("location", location);
        model.addAttribute("sort", sort);
        return "community/communities_list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("dto", new CommunityRequestDto());
        return "community/community_form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("dto") CommunityRequestDto dto, BindingResult br, Model model) {
        if (br.hasErrors()) return "community/community_form";
        Community c = communityService.create(dto);
        return "redirect:/communities/" + c.getId();
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Community c = communityService.browse(null, null, null, PageRequest.of(0, 1))
                .getContent().stream().filter(x -> x.getId().equals(id)).findFirst()
                .orElseThrow(); // or communityRepo.findById(id)
        CommunityRequestDto dto = new CommunityRequestDto();
        dto.setName(c.getName());
        dto.setDescription(c.getDescription());
        dto.setCategory(c.getCategory());
        dto.setLocation(c.getLocation());
        dto.setCoverImageUrl(c.getCoverImageUrl());
        model.addAttribute("dto", dto);
        model.addAttribute("communityId", id);
        return "community/community_form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("dto") CommunityRequestDto dto, BindingResult br) {
        if (br.hasErrors()) return "community/community_form";
        communityService.update(id, dto);
        return "redirect:/communities/" + id;
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "10") int size,
                         Model model) {

        Community c = communityService.findById(id);
        var posts = communityPostService.pageByCommunity(id, PageRequest.of(page, size));
        Long userId = null;
        try {
            userId = auth.getLoggedInUser().getId();
        } catch (Exception ignored) {
        }
        boolean isMember = (userId != null) && communityService.isMember(id, userId);
        long memberCount = communityService.memberCount(id);

        model.addAttribute("community", c);
        model.addAttribute("posts", posts);
        model.addAttribute("isMember", isMember);
        model.addAttribute("memberCount", memberCount);
        model.addAttribute("postDto", new PostRequestDto());

        return "community/community_detail";
    }
}
