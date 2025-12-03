// src/main/java/com/petport/home/HomeController.java
package com.example.capstone.home;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final HomeService homeService;

    @GetMapping({"/", "/home"})
    public String index(Model model) {
        model.addAttribute("latestPets", homeService.latestPets());
        model.addAttribute("latestPosts", homeService.latestPosts());
        model.addAttribute("upcomingEvents", homeService.upcomingEvents());
        return "index";
    }
}
