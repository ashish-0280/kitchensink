package com.example.kitchensink.service;

import com.example.kitchensink.dto.LoginRequestDto;
import com.example.kitchensink.dto.SignupRequestDto;
import com.example.kitchensink.exception.DuplicateResourceException;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import com.example.kitchensink.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    PasswordEncoder encoder;

    @Mock
    JwtService jwtService;

    @InjectMocks
    AuthService authService;

    @Test
    void register_shouldCreateUser() {

        SignupRequestDto dto = new SignupRequestDto(
                "Ashish",
                "ashish@gmail.com",
                "999999",
                "password"
        );

        when(memberRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(encoder.encode(dto.getPassword())).thenReturn("encoded");

        authService.register(dto);

        verify(memberRepository,times(1)).save(any(Member.class));
    }

    @Test
    void register_shouldThrowDuplicateException(){

        SignupRequestDto dto = new SignupRequestDto();
        dto.setEmail("test@gmail.com");

        when(memberRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                ()-> authService.register(dto));
    }

}