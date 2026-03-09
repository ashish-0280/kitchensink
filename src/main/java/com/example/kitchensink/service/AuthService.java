package com.example.kitchensink.service;

import com.example.kitchensink.dto.LoginRequestDto;
import com.example.kitchensink.dto.LoginResponseDto;
import com.example.kitchensink.dto.SignupRequestDto;
import com.example.kitchensink.dto.SignupResponseDto;
import com.example.kitchensink.exception.DuplicateResourceException;
import com.example.kitchensink.exception.InvalidCredentialsException;
import com.example.kitchensink.exception.ResourceNotFoundException;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import com.example.kitchensink.security.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public AuthService(MemberRepository memberRepository, PasswordEncoder encoder, JwtService jwtService) {
        this.memberRepository = memberRepository;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    public SignupResponseDto register(SignupRequestDto signupRequestDto) {

        if(memberRepository.existsByEmail(signupRequestDto.getEmail())){
            throw new DuplicateResourceException("Email already registered");
        }
        Member member = new Member();
        member.setName(signupRequestDto.getName());
        member.setEmail(signupRequestDto.getEmail());
        member.setPhone(signupRequestDto.getPhone());
        member.setPassword(encoder.encode(signupRequestDto.getPassword()));
        member.setRole("USER");
        memberRepository.save(member);
        SignupResponseDto response = new SignupResponseDto();
        response.setEmail(member.getEmail());
        response.setMessage("User registered successfully!");
        return response;
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        Member member = memberRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with email: " + loginRequestDto.getEmail()
                        )
                );
        if (!encoder.matches(loginRequestDto.getPassword(), member.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
        String token = jwtService.generateToken(member);
        LoginResponseDto response = new LoginResponseDto();
        response.setToken(token);
        response.setRole(member.getRole());
        response.setEmail(member.getEmail());
        return response;
    }
}
