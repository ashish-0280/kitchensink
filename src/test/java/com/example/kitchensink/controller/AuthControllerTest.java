package com.example.kitchensink.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.kitchensink.dto.LoginRequestDto;
import com.example.kitchensink.dto.LoginResponseDto;
import com.example.kitchensink.dto.SignupRequestDto;
import com.example.kitchensink.dto.SignupResponseDto;
import com.example.kitchensink.service.AuthService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Tests")
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }

    // ─── Helper builders ─────────────────────────────────────────────────────────

    private SignupRequestDto validSignupRequest() {
        SignupRequestDto dto = new SignupRequestDto();
        dto.setName("John Doe");
        dto.setEmail("john@example.com");
        dto.setPassword("Password@123");
        dto.setPhone("1234567890");
        return dto;
    }

    private SignupResponseDto sampleSignupResponse() {
        SignupResponseDto dto = new SignupResponseDto();
        dto.setEmail("john@example.com");
        return dto;
    }

    private LoginRequestDto validLoginRequest() {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("john@example.com");
        dto.setPassword("Password@123");
        return dto;
    }

    private LoginResponseDto sampleLoginResponse() {
        LoginResponseDto dto = new LoginResponseDto();
        dto.setToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test-token");
        dto.setEmail("john@example.com");
        return dto;
    }


    @Nested
    @DisplayName("POST /auth/signup")
    class SignupTests {

        @Test
        @DisplayName("Should return 200 OK with SignupResponseDto when request is valid")
        void signup_WithValidRequest_ReturnsOk() throws Exception {
            SignupRequestDto request = validSignupRequest();
            SignupResponseDto response = sampleSignupResponse();

            when(authService.register(any(SignupRequestDto.class))).thenReturn(response);

            mockMvc.perform(post("/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("abc123"))
                    .andExpect(jsonPath("$.email").value("john@example.com"));

            verify(authService, times(1)).register(any(SignupRequestDto.class));
        }

        @Test
        @DisplayName("Should return 400 Bad Request when request body is empty")
        void signup_WithEmptyBody_ReturnsBadRequest() throws Exception {
            mockMvc.perform(post("/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());

            verify(authService, never()).register(any(SignupRequestDto.class));
        }

        @Test
        @DisplayName("Should return 400 Bad Request when email is invalid")
        void signup_WithInvalidEmail_ReturnsBadRequest() throws Exception {
            SignupRequestDto request = validSignupRequest();
            request.setEmail("not-an-email");

            mockMvc.perform(post("/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(authService, never()).register(any(SignupRequestDto.class));
        }

        @Test
        @DisplayName("Should return 400 Bad Request when password is blank")
        void signup_WithBlankPassword_ReturnsBadRequest() throws Exception {
            SignupRequestDto request = validSignupRequest();
            request.setPassword("");

            mockMvc.perform(post("/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(authService, never()).register(any(SignupRequestDto.class));
        }
    }

    @Nested
    @DisplayName("GET /auth/signup")
    class ShowSignupFormTests {

        @Test
        @DisplayName("Should return Signup view with empty signupRequest model attribute")
        void showForm_ReturnsSignupView() throws Exception {
            mockMvc.perform(get("/auth/signup"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("Signup"))
                    .andExpect(model().attributeExists("signupRequest"));

            verifyNoInteractions(authService);
        }

        @Test
        @DisplayName("Should not call AuthService when rendering signup form")
        void showForm_DoesNotCallAuthService() throws Exception {
            mockMvc.perform(get("/auth/signup"))
                    .andExpect(status().isOk());

            verifyNoInteractions(authService);
        }
    }

    @Nested
    @DisplayName("GET /auth/login")
    class ShowLoginFormTests {

        @Test
        @DisplayName("Should return Login view with empty user model attribute")
        void showLogin_ReturnsLoginView() throws Exception {
            mockMvc.perform(get("/auth/login"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("Login"))
                    .andExpect(model().attributeExists("user"));

            verifyNoInteractions(authService);
        }

        @Test
        @DisplayName("Should not call AuthService when rendering login form")
        void showLogin_DoesNotCallAuthService() throws Exception {
            mockMvc.perform(get("/auth/login"))
                    .andExpect(status().isOk());

            verifyNoInteractions(authService);
        }
    }


    @Nested
    @DisplayName("POST /auth/login")
    class LoginTests {

        @Test
        @DisplayName("Should return 200 OK with LoginResponseDto when credentials are valid")
        void login_WithValidCredentials_ReturnsOk() throws Exception {
            LoginRequestDto request = validLoginRequest();
            LoginResponseDto response = sampleLoginResponse();

            when(authService.login(any(LoginRequestDto.class))).thenReturn(response);

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value(response.getToken()))
                    .andExpect(jsonPath("$.email").value(response.getEmail()));

            verify(authService, times(1)).login(any(LoginRequestDto.class));
        }

        @Test
        @DisplayName("Should set HttpOnly secure cookie with token on successful login")
        void login_WithValidCredentials_SetsCookieInResponse() throws Exception {
            LoginRequestDto request = validLoginRequest();
            LoginResponseDto response = sampleLoginResponse();

            when(authService.login(any(LoginRequestDto.class))).thenReturn(response);

            MvcResult result = mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andReturn();

            String setCookieHeader = result.getResponse().getHeader("Set-Cookie");
            assertThat(setCookieHeader).isNotNull();
            assertThat(setCookieHeader).contains("token=");
            assertThat(setCookieHeader).contains("HttpOnly");
            assertThat(setCookieHeader).contains("Secure");
            assertThat(setCookieHeader).contains("SameSite=Strict");
            assertThat(setCookieHeader).contains("Path=/");
        }

        @Test
        @DisplayName("Should set cookie with 1-hour max age on successful login")
        void login_WithValidCredentials_SetsCookieWithCorrectMaxAge() throws Exception {
            LoginRequestDto request = validLoginRequest();
            LoginResponseDto response = sampleLoginResponse();

            when(authService.login(any(LoginRequestDto.class))).thenReturn(response);

            MvcResult result = mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andReturn();

            String setCookieHeader = result.getResponse().getHeader("Set-Cookie");
            assertThat(setCookieHeader).contains("Max-Age=3600");
        }

        @Test
        @DisplayName("Should call AuthService login exactly once per request")
        void login_CallsAuthServiceOnce() throws Exception {
            when(authService.login(any(LoginRequestDto.class))).thenReturn(sampleLoginResponse());

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validLoginRequest())))
                    .andExpect(status().isOk());

            verify(authService, times(1)).login(any(LoginRequestDto.class));
        }
    }
}