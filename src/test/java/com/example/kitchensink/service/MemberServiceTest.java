package com.example.kitchensink.service;

import com.example.kitchensink.dto.MemberRequestDto;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    MemberRepository repository;

    @Mock
    ModelMapper mapper;

    @Mock
    PasswordEncoder encoder;

    @InjectMocks
    MemberService service;

    @Test
    void create_shouldSaveMember(){

        MemberRequestDto dto = new MemberRequestDto();
        dto.setEmail("test@gmail.com");

        when(repository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(encoder.encode(any())).thenReturn("encoded");

        service.create(dto);

        verify(repository).save(any(Member.class));
    }
}