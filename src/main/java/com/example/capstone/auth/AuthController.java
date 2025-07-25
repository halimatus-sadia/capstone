package com.example.capstone.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRequest());
        model.addAttribute("roles", List.of(Map.of("value", "ROLE_INSTRUCTOR", "text", "Instructor"),
                Map.of("value", "ROLE_USER", "text", "User")));
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") @Valid UserRequest userRequest, Model model) {
        userService.registerUser(userRequest);
        model.addAttribute("message", "Registration successful.");
        return "login";
    }

    @GetMapping("/login")
    public String login(
            @RequestParam(required = false) String error,
            @RequestParam(name = "next", required = false) String next,
            Model model) {
        model.addAttribute("error", error);
        if (StringUtils.hasText(next)) {
            model.addAttribute("next", URLEncoder.encode(next, StandardCharsets.UTF_8));
        }
        return "login";
    }
}
