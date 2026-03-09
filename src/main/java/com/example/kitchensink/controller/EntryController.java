package com.example.kitchensink.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EntryController {

    @GetMapping("/kitchensink")
    public String entry(Authentication authentication) {

        if(authentication == null || !authentication.isAuthenticated()){
            return "redirect:/auth/login";
        }

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if(isAdmin){
            return "redirect:/admin/members";
        }

        return "redirect:/member/profile";
    }
}