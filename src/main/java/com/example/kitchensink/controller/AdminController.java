package com.example.kitchensink.controller;

import com.example.kitchensink.dto.MemberRequestDto;
import com.example.kitchensink.dto.MemberResponseDto;
import com.example.kitchensink.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final MemberService memberService;

    public AdminController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/members")
    public String getAllMembers(Model model) {
        List<MemberResponseDto> list = memberService.getAll();
        model.addAttribute("allMembers", list);
        return "AdminDashboard";
    }

    @GetMapping("/members/{id}")
    public ResponseEntity<MemberResponseDto> getMember(@PathVariable String id){
        return ResponseEntity.ok(memberService.getById(id));
    }

    @GetMapping("/members/profile/{id}")
    public String viewMemberProfile(@PathVariable String id, Model model){

        MemberResponseDto member = memberService.getById(id);

        model.addAttribute("user", member);

        return "profile";
    }

    @DeleteMapping("/members/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteMember(@PathVariable String id){
        memberService.delete(id);
        return ResponseEntity.ok("Deleted");
    }
}