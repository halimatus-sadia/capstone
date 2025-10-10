package com.example.capstone.common;

import com.example.capstone.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

@RequiredArgsConstructor
@ControllerAdvice
public class GlobalExceptionHandler {

    private final AuthUtils authUtils;

    @ExceptionHandler(Exception.class)
    public String handleGlobalException(Model model, Exception ex) {
        model.addAttribute("message", ex.getMessage());
        System.out.println("exception occurred:");
        ex.printStackTrace();
        return "error";
    }

    @ModelAttribute("currentUserId")
    public Long populateCurrentUserId(@AuthenticationPrincipal User user) {
        if (user == null) {
            return null;
        }
        return authUtils.getLoggedInUser().getId();
    }
}
