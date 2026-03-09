package com.example.kitchensink.controller;

import com.example.kitchensink.dto.MemberResponseDto;
import com.example.kitchensink.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final MemberService memberService;

    public AdminController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/members")
    public String getAllMembers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model
    ){

        Pageable pageable = PageRequest.of(page,size);

        Page<MemberResponseDto> members =
                memberService.searchMembers(keyword,pageable);

        model.addAttribute("allMembers",members.getContent());
        model.addAttribute("currentPage",page);
        model.addAttribute("totalPages",members.getTotalPages());
        model.addAttribute("keyword",keyword);

        return "AdminDashboard";
    }

    @GetMapping("/members/search")
    @ResponseBody
    public Page<MemberResponseDto> searchMembers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ){

        Pageable pageable = PageRequest.of(page,size);

        return memberService.searchMembers(keyword,pageable);
    }

    @DeleteMapping("/members/{id}")
    @ResponseBody
    public String deleteMember(@PathVariable String id){
        memberService.delete(id);
        return "Deleted";
    }

}