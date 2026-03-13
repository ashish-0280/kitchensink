package com.example.kitchensink.controller;

import com.example.kitchensink.dto.*;
import com.example.kitchensink.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    @ResponseBody
    public ResponseEntity<SignupResponseDto> signup(
            @RequestBody SignupRequestDto signupRequestDto) {

        SignupResponseDto response = authService.register(signupRequestDto);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/signup")
    public String showForm(Model model){
        model.addAttribute("signupRequest", new SignupRequestDto());
        return "Signup";
    }

    @GetMapping("/login")
    public String showLogin(Model model){
        model.addAttribute("user", new LoginRequestDto());
        return "Login";
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<LoginResponseDto> login(
            @RequestBody LoginRequestDto loginRequestDto,
            HttpServletResponse response) {

        LoginResponseDto loginResponse = authService.login(loginRequestDto);

        ResponseCookie cookie = ResponseCookie.from("token", loginResponse.getToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(60 * 60)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok(loginResponse);
    }
}

