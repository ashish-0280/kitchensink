package com.example.kitchensink.controller;

import com.example.kitchensink.service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AuthService authService;

    @Test
    void signup_shouldReturn200() throws Exception {

        String body = """
                {
                "name":"test",
                "email":"test@gmail.com",
                "phone":"9999",
                "password":"pass"
                }
                """;

        mockMvc.perform(post("/auth/signup")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isOk());
    }
}