package com.example.capstone.petstore;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MarketplaceController {
    private final ProductService productService;

    @GetMapping("/marketplace")
    public String marketplace(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "petstore/marketplace";
    }
}
