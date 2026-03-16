package com.example.kitchensink.security;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.service.BlacklistToken;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    JwtService jwtService = new JwtService(new BlacklistToken());

    @Test
    void generateToken_shouldWork(){

        Member member = new Member();
        member.setEmail("test@gmail.com");
        member.setRole("USER");

        String token = jwtService.generateToken(member);

        assertNotNull(token);
    }

}