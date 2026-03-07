package com.example.kitchensink.controller;

import com.example.kitchensink.config.MapperConfig;
import com.example.kitchensink.dto.MemberRequestDto;
import com.example.kitchensink.dto.MemberResponseDto;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import com.example.kitchensink.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Slf4j
@RestController
public class DashboardController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    public DashboardController(MemberService memberService, MemberRepository memberRepository, ModelMapper modelMapper) {
        this.memberService = memberService;
        this.memberRepository = memberRepository;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/member/profile")
    public MemberResponseDto memberProfile(Principal principal) {
        String email = principal.getName();
        log.info("email {}", email);
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User Not Found") );
        return modelMapper.map(member, MemberResponseDto.class);
    }
}
