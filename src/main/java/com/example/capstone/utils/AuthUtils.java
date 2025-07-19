package com.example.capstone.utils;

import com.example.capstone.auth.User;
import com.example.capstone.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUtils {

    private final UserRepository userRepository;

    @NotNull
    public User getLoggedInUser() {
        var principal = (org.springframework.security.core.userdetails.User)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(principal.getUsername()).orElseThrow();
    }
}
