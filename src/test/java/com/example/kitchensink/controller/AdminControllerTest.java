package com.example.kitchensink.controller;

import com.example.kitchensink.dto.MemberResponseDto;
import com.example.kitchensink.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @Test
    void getAllMembers_ReturnsDashboardView() throws Exception {
        Page<MemberResponseDto> page = new PageImpl<>(Collections.emptyList());
        when(memberService.searchMembers(any(), any())).thenReturn(page); [cite: 17]

        mockMvc.perform(get("/admin/members"))
                .andExpect(status().isOk())
                .andExpect(view().name("AdminDashboard"))
                .andExpect(model().attributeExists("allMembers")); [cite: 17]
    }

    @Test
    void deleteMember_ReturnsSuccessString() throws Exception {
        doNothing().when(memberService).delete(anyString()); [cite: 68]

        mockMvc.perform(delete("/admin/members/123"))
                .andExpect(status().isOk())
                .andExpect(content().string("Deleted")); [cite: 20]
    }

    @Test
    void getMemberProfile_ReturnsProfileView() throws Exception {
        MemberResponseDto member = new MemberResponseDto();
        when(memberService.getById("123")).thenReturn(member); [cite: 67]

        mockMvc.perform(get("/admin/members/profile/123"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attribute("isAdminView", true)); [cite: 20]
    }

    @Test
    void searchMembers_ReturnsJsonPage() throws Exception {
        Page<MemberResponseDto> page = new PageImpl<>(Collections.emptyList());
        when(memberService.searchMembers(any(), any())).thenReturn(page); [cite: 18]

        mockMvc.perform(get("/admin/members/search")
                        .param("keyword", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray()); [cite: 18]
    }

    @Test
    void editMember_ReturnsEditView() throws Exception {
        MemberResponseDto member = new MemberResponseDto();
        when(memberService.getById("123")).thenReturn(member); [cite: 14]

        mockMvc.perform(get("/admin/members/edit/123"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-profile")); [cite: 15]
    }

    @Test
    void updateMember_RedirectsToProfile() throws Exception {
        mockMvc.perform(post("/admin/members/update/123")
                        .param("name", "Updated Name"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/members/profile/123")); [cite: 15]
    }
}