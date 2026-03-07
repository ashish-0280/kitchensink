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

//    @PutMapping("/members/{id}")
//    public ResponseEntity<MemberResponseDto> updateMember(@PathVariable String id, @Valid @RequestBody MemberRequestDto member) {
//
//        return ResponseEntity.ok(memberService.update(id, member));
//    }
//
//    @DeleteMapping("/members/{id}")
//    public ResponseEntity<Void> deleteMember(@PathVariable String id) {
//        memberService.delete(id);
//        return ResponseEntity.noContent().build();
//    }
}