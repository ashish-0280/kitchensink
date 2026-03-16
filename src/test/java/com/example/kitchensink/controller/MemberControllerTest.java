package com.example.kitchensink.controller;

import com.example.kitchensink.dto.MemberResponseDto;
import com.example.kitchensink.service.MemberService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    MemberService memberService;

    @Test
    @WithMockUser(roles = {"USER"})
    void getProfile_shouldReturn200() throws Exception {

        MemberResponseDto dto = new MemberResponseDto();
        dto.setEmail("test@gmail.com");

        Mockito.when(memberService.getByEmail(Mockito.any()))
                .thenReturn(dto);

        mockMvc.perform(get("/member/profile"))
                .andExpect(status().isOk());
    }
}