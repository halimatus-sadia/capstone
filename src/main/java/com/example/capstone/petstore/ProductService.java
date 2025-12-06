package com.example.capstone.petstore;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final List<Product> products = new ArrayList<>();

    public ProductService() {
        products.addAll(Arrays.asList(
                new Product(1L, "Premium Dog Food", "High-quality grain-free dog food.", 49.99,
                        "https://images.unsplash.com/photo-1568640347023-a616a30bc3bd?auto=format&fit=crop&w=500&q=60"),
                new Product(2L, "Interactive Cat Toy", "Keeps your cat entertained for hours.", 19.99,
                        "https://images.unsplash.com/photo-1545249390-6bdfa286032f?auto=format&fit=crop&w=500&q=60"),
                new Product(3L, "Cozy Pet Bed", "Soft and plush bed for ultimate comfort.", 39.99,
                        "https://images.unsplash.com/photo-1596272875729-ed2c18bb7f97?auto=format&fit=crop&w=500&q=60"),
                new Product(4L, "Durable Leash", "Strong leash for active dogs.", 15.99,
                        "https://images.unsplash.com/photo-1576201836106-db1758fd1c97?auto=format&fit=crop&w=500&q=60"),
                new Product(5L, "Bird Cage Bundle", "Spacious cage with accessories.", 89.99,
                        "https://images.unsplash.com/photo-1552728089-57bdde30ebd1?auto=format&fit=crop&w=500&q=60"),
                new Product(6L, "Aquarium Kit", "Start your own underwater world.", 129.99,
                        "https://images.unsplash.com/photo-1522069169874-c58ec4b76be5?auto=format&fit=crop&w=500&q=60")));
    }

    public List<Product> getAllProducts() {
        return products;
    }

    public Optional<Product> getProductById(Long id) {
        return products.stream().filter(p -> p.getId().equals(id)).findFirst();
    }
}
