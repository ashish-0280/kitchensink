package com.example.kitchensink.controller;

import com.example.kitchensink.dto.MemberRequestDto;
import com.example.kitchensink.dto.MemberResponseDto;
import com.example.kitchensink.service.MemberService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
public class KitchensinkController {

    private final MemberService memberService;

    public KitchensinkController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/kitchensink-form")
    public String showForm(Model model){
        model.addAttribute("user", new MemberRequestDto());
        return "kitchensink-form";
    }

    @PostMapping("/kitchensink")
    public String createMember(MemberRequestDto member, HttpSession session) {
        MemberResponseDto response = memberService.create(member);
        response.setRole("MEMBER");
        session.setAttribute("user", response);
        return "redirect:/my-profile";
    }

    @GetMapping("/my-profile")
    public String showData(Model model, HttpSession session){
        MemberResponseDto user = (MemberResponseDto) session.getAttribute("user");
        model.addAttribute("user", user);
        return "profile";
    }
}
