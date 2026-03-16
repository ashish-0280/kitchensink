package com.example.kitchensink.service;

import com.example.kitchensink.dto.*;
import com.example.kitchensink.exception.*;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import com.example.kitchensink.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock private MemberRepository memberRepository;
    @Mock private PasswordEncoder encoder;
    @Mock private JwtService jwtService;

    @InjectMocks private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_Success() {
        SignupRequestDto dto = new SignupRequestDto("User", "test@test.com", "1234567890", "password123");
        when(memberRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(encoder.encode(any())).thenReturn("encodedPass");

        SignupResponseDto response = authService.register(dto);

        assertNotNull(response);
        assertEquals("test@test.com", response.getEmail());
        verify(memberRepository, times(1)).save(any());
    }

    @Test
    void register_ThrowsDuplicateException() {
        SignupRequestDto dto = new SignupRequestDto();
        dto.setEmail("exists@test.com");
        when(memberRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(dto));
    }

    @Test
    void login_Success() {
        LoginRequestDto dto = new LoginRequestDto("test@test.com", "password123");
        Member member = new Member();
        member.setEmail("test@test.com");
        member.setPassword("encodedPass");
        member.setRole("USER");

        when(memberRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(member));
        when(encoder.matches(dto.getPassword(), member.getPassword())).thenReturn(true);
        when(jwtService.generateToken(member)).thenReturn("mock-jwt-token");

        LoginResponseDto response = authService.login(dto);

        assertEquals("mock-jwt-token", response.getToken());
        assertEquals("USER", response.getRole());
    }

    @Test
    void login_ThrowsUserNotFound() {
        LoginRequestDto dto = new LoginRequestDto("wrong@test.com", "pass");
        when(memberRepository.findByEmail(any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.login(dto));
    }

    @Test
    void login_ThrowsInvalidCredentials() {
        LoginRequestDto dto = new LoginRequestDto("test@test.com", "wrongpass");
        Member member = new Member();
        member.setPassword("encodedPass");

        when(memberRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(member));
        when(encoder.matches(any(), any())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(dto));
    }

    @Test
    void register_VerifiesRoleIsUser() {
        SignupRequestDto dto = new SignupRequestDto("User", "test@test.com", "1234567890", "password");
        when(memberRepository.existsByEmail(any())).thenReturn(false);

        authService.register(dto);

        verify(memberRepository).save(argThat(member -> member.getRole().equals("USER")));
    }
}