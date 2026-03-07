package com.example.kitchensink.controller;

import com.example.kitchensink.dto.MemberResponseDto;
import com.example.kitchensink.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    //TODO Member can update itself
    @GetMapping("/{id}")
    public ResponseEntity<MemberResponseDto> getMember(@PathVariable String id) {
        return ResponseEntity.ok(memberService.getById(id));
    }
}