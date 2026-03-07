package com.example.kitchensink.service;

import com.example.kitchensink.dto.MemberRequestDto;
import com.example.kitchensink.dto.MemberResponseDto;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private MemberService memberService;

    private Member member;
    private MemberRequestDto requestDto;
    private MemberResponseDto responseDto;

    @BeforeEach
    void setUp() {
        member = new Member();
        member.setId("1");
        member.setName("Kamil");
        requestDto = new MemberRequestDto();
        requestDto.setName("Kamil");
        responseDto = new MemberResponseDto();
        responseDto.setId("1");
        responseDto.setName("Kamil");
    }
    @Test
    void testCreate() {
        when(modelMapper.map(requestDto, Member.class)).thenReturn(member);
        when(memberRepository.save(member)).thenReturn(member);
        when(modelMapper.map(member, MemberResponseDto.class)).thenReturn(responseDto);
        MemberResponseDto result = memberService.create(requestDto);
        assertNotNull(result);
        assertEquals(requestDto.getName(), result.getName());
        verify(memberRepository, times(1)).save(member);
    }
    @Test
    void testGetById() {
        when(memberRepository.findById("1")).thenReturn(Optional.of(member));
        when(modelMapper.map(member, MemberResponseDto.class)).thenReturn(responseDto);
        MemberResponseDto result = memberService.getById("1");
        assertEquals("1", result.getId());
    }

    @Test
    void testGetByIdNotFound() {
        when(memberRepository.findById("1")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> memberService.getById("1"));
    }

    @Test
    void testDelete() {
        memberService.delete("1");
        verify(memberRepository, times(1)).deleteById("1");
    }
}