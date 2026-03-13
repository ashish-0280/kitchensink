package com.example.kitchensink.controller;

import com.example.kitchensink.security.JwtService;
import com.example.kitchensink.service.BlacklistToken;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LogoutController {

    private final BlacklistToken blacklistToken;
    private final JwtService jwtService;

    public LogoutController(BlacklistToken blacklistToken, JwtService jwtService) {
        this.blacklistToken = blacklistToken;
        this.jwtService = jwtService;
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request){
        String token = jwtService.getTokenFromCookie(request);
        blacklistToken.blacklistToken(token);

        return "redirect:/Login";
    }
}
