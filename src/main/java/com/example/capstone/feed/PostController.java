package com.example.capstone.feed;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public String list(@RequestParam(value = "q", required = false) String q,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "size", defaultValue = "12") int size,
                       @RequestParam(value = "sort", defaultValue = "createdAt,desc") String sort,
                       Model model) {

        Sort s;
        if (sort.contains(",")) {
            String[] parts = sort.split(",", 2);
            s = "desc".equalsIgnoreCase(parts[1]) ? Sort.by(parts[0]).descending() : Sort.by(parts[0]).ascending();
        } else {
            s = Sort.by(sort).descending();
        }

        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), s);
        Page<Post> posts = postService.list(q, pageable);

        model.addAttribute("posts", posts);
        model.addAttribute("q", q);
        model.addAttribute("sort", sort);
        return "posts/index";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("post", new PostRequest());
        return "posts/create";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("post") PostRequest req,
                         BindingResult bindingResult,
                         Authentication auth,
                         RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            return "posts/create";
        }
        if (auth == null) {
            ra.addFlashAttribute("error", "You must be logged in to create a post.");
            return "redirect:/login?next=/posts/create";
        }
        Post created = postService.create(auth.getName(), req);
        ra.addFlashAttribute("success", "Post created.");
        return "redirect:/posts/" + created.getId();
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Long id, Authentication auth, Model model, RedirectAttributes ra) {
        Post p = postService.get(id);
        if (auth == null || !auth.getName().equals(p.getCreatedBy().getUsername())) {
            ra.addFlashAttribute("error", "Only the author can edit this post.");
            return "redirect:/posts/" + id;
        }
        PostRequest req = new PostRequest();
        req.setTitle(p.getTitle());
        req.setContent(p.getContent());
        req.setImageUrl(p.getImageUrl());
        model.addAttribute("post", req);
        model.addAttribute("postId", id);
        return "posts/update";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("post") PostRequest req,
                         BindingResult bindingResult,
                         Authentication auth,
                         RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            return "posts/update";
        }
        if (auth == null) {
            ra.addFlashAttribute("error", "You must be logged in.");
            return "redirect:/login?next=/posts/update/" + id;
        }
        postService.update(id, auth.getName(), req);
        ra.addFlashAttribute("success", "Post updated.");
        return "redirect:/posts/" + id;
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Authentication auth, Model model) {
        Post p = postService.get(id);
        model.addAttribute("post", p);
        boolean isAuthor = auth != null && auth.getName().equals(p.getCreatedBy().getUsername());
        model.addAttribute("isAuthor", isAuthor);
        return "posts/detail";
    }
}
