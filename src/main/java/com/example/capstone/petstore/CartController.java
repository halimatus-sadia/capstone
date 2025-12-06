// src/main/java/com/yourpackage/controller/CartController.java
package com.example.capstone.petstore;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final ProductService productService;

    public CartController(CartService cartService,
                          ProductService productService) {
        this.cartService = cartService;
        this.productService = productService;
    }

    @GetMapping
    public String viewCart(Model model) {
        model.addAttribute("cartItems", cartService.getCartItems());
        model.addAttribute("total", cartService.getTotal());
        return "petstore/cart";
    }

    @GetMapping("/add/{id}")
    public String addToCart(@PathVariable Long id) {
        productService.getProductById(id)
                .ifPresent(cartService::addToCart);
        return "redirect:/marketplace";
    }

    @GetMapping("/remove/{id}")
    public String removeFromCart(@PathVariable Long id) {
        cartService.removeFromCartById(id);
        return "redirect:/cart";
    }
}
