package com.example.kitchensink.controller;

import com.example.kitchensink.dto.MemberResponseDto;
import com.example.kitchensink.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String getProfile(Model model, Principal principal) {

        String email;
        if(SecurityContextHolder.getContext().getAuthentication() != null){
            email = SecurityContextHolder.getContext().getAuthentication().getName();
        } else {
            email = principal.getName();
        }

        MemberResponseDto member = memberService.getByEmail(email);

        model.addAttribute("user", member);

        return "profile";
    }

    //TODO Member can update itself
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MemberResponseDto> getMember(@PathVariable String id) {
        return ResponseEntity.ok(memberService.getById(id));
    }
}