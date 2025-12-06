package com.example.capstone.petstore;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.List;

@Service
@SessionScope
public class CartService {

    private final List<Product> cartItems = new ArrayList<>();

    public void addToCart(Product product) {
        cartItems.add(product);
    }

    public void removeFromCart(Product product) {
        cartItems.remove(product);
    }

    public void removeFromCartById(Long id) {
        cartItems.removeIf(p -> p.getId().equals(id));
    }

    public List<Product> getCartItems() {
        return cartItems;
    }

    public double getTotal() {
        return cartItems.stream().mapToDouble(Product::getPrice).sum();
    }

    public void clearCart() {
        cartItems.clear();
    }
}
