package com.example.kitchensink.controller;

import com.example.kitchensink.dto.*;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signup(@Valid @RequestBody SignupRequestDto signupRequestDto) {
        SignupResponseDto response = authService.register(signupRequestDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto>login(@Valid @RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {

        LoginResponseDto loginResponse = authService.login(loginRequestDto);
        log.info("loginResponse {}", loginResponse);
        ResponseCookie cookie = ResponseCookie.from("token", loginResponse.getToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(60 * 60)
                .sameSite("None").domain("localhost")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
        return ResponseEntity.ok(loginResponse);
    }
}

