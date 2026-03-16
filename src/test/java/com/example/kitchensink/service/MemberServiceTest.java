package com.example.kitchensink.service;

import com.example.kitchensink.dto.MemberRequestDto;
import com.example.kitchensink.dto.MemberResponseDto;
import com.example.kitchensink.exception.ResourceNotFoundException;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MemberServiceTest {

    @Mock private MemberRepository memberRepository;
    @Mock private ModelMapper modelMapper;
    @Mock private PasswordEncoder encoder;

    @InjectMocks private MemberService memberService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getById_Success() {
        Member member = new Member();
        member.setId("123");
        MemberResponseDto responseDto = new MemberResponseDto();
        responseDto.setId("123");

        when(memberRepository.findById("123")).thenReturn(Optional.of(member));
        when(modelMapper.map(member, MemberResponseDto.class)).thenReturn(responseDto);

        MemberResponseDto result = memberService.getById("123");

        assertEquals("123", result.getId());
    }

    @Test
    void getById_ThrowsNotFound() {
        when(memberRepository.findById("999")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> memberService.getById("999"));
    }

    @Test
    void update_Success() {
        Member member = new Member();
        member.setId("123");
        MemberRequestDto dto = new MemberRequestDto();
        dto.setName("New Name");

        when(memberRepository.findById("123")).thenReturn(Optional.of(member));
        when(memberRepository.save(any())).thenReturn(member);
        when(modelMapper.map(any(), eq(MemberResponseDto.class))).thenReturn(new MemberResponseDto());

        assertNotNull(memberService.update("123", dto));
        verify(memberRepository).save(member);
    }

    @Test
    void delete_Success() {
        doNothing().when(memberRepository).deleteById("123");
        memberService.delete("123");
        verify(memberRepository, times(1)).deleteById("123");
    }

    @Test
    void searchMembers_WithKeyword() {
        PageRequest pageable = PageRequest.of(0, 5);
        Member member = new Member();
        Page<Member> page = new PageImpl<>(Collections.singletonList(member));

        when(memberRepository.searchMembers(eq("test"), any())).thenReturn(page);
        when(modelMapper.map(any(), eq(MemberResponseDto.class))).thenReturn(new MemberResponseDto());

        Page<MemberResponseDto> result = memberService.searchMembers("test", pageable);

        assertFalse(result.isEmpty());
        verify(memberRepository).searchMembers(eq("test"), any());
    }

    @Test
    void getByEmail_Success() {
        Member member = new Member();
        member.setEmail("test@test.com");
        when(memberRepository.findByEmail("test@test.com")).thenReturn(Optional.of(member));
        when(modelMapper.map(any(), any())).thenReturn(new MemberResponseDto());

        memberService.getByEmail("test@test.com");
        verify(memberRepository).findByEmail("test@test.com");
    }
}