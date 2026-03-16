package com.example.kitchensink.controller;

import com.example.kitchensink.dto.*;
import com.example.kitchensink.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Disabling security filters for unit testing controllers
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void showSignupForm_ReturnsSignupView() throws Exception {
        mockMvc.perform(get("/auth/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("Signup"))
                .andExpect(model().attributeExists("signupRequest")); [cite: 23]
    }

    @Test
    void signup_Success_ReturnsJsonResponse() throws Exception {
        SignupRequestDto request = new SignupRequestDto("John", "john@test.com", "1234567890", "pass1234");
        SignupResponseDto response = new SignupResponseDto("john@test.com", "User registered successfully!"); [cite: 59]

        when(authService.register(any())).thenReturn(response);

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@test.com")); [cite: 22]
    }

    @Test
    void showLoginForm_ReturnsLoginView() throws Exception {
        mockMvc.perform(get("/auth/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("Login")); [cite: 23]
    }

    @Test
    void login_Success_SetsCookieAndReturnsDto() throws Exception {
        LoginRequestDto request = new LoginRequestDto("john@test.com", "pass1234");
        LoginResponseDto response = new LoginResponseDto("john@test.com", "mock-token", "USER"); [cite: 61]

        when(authService.login(any())).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().exists("Set-Cookie"))
                .andExpect(jsonPath("$.role").value("USER")); [cite: 24]
    }

    @Test
    void signup_InvalidData_ReturnsBadRequest() throws Exception {
        // Sending empty name to trigger validation (assuming validation is active)
        SignupRequestDto invalidRequest = new SignupRequestDto("", "bad-email", "123", "1");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void logout_RedirectsToLogin() throws Exception {
        mockMvc.perform(get("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/Login")); [cite: 27]
    }
}