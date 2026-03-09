package com.example.kitchensink.service;

import com.example.kitchensink.dto.MemberRequestDto;
import com.example.kitchensink.dto.MemberResponseDto;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    public MemberService(MemberRepository memberRepository, ModelMapper modelMapper) {
        this.memberRepository = memberRepository;
        this.modelMapper = modelMapper;
    }

    public MemberResponseDto create(MemberRequestDto requestDto) {
        Member member = modelMapper.map(requestDto, Member.class);
        Member saved = memberRepository.save(member);
        return modelMapper.map(saved, MemberResponseDto.class);
    }

    public List<MemberResponseDto> getAll() {
        return memberRepository.findAll()
                .stream()
                .map(member -> modelMapper.map(member, MemberResponseDto.class))
                .collect(Collectors.toList());
    }

    public MemberResponseDto getById(String id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        return modelMapper.map(member, MemberResponseDto.class);
    }

    public MemberResponseDto update(String id, MemberRequestDto requestDto) {
        Member existing = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        modelMapper.map(requestDto, existing);
        Member updated = memberRepository.save(existing);
        return modelMapper.map(updated, MemberResponseDto.class);
    }
    public MemberResponseDto getByEmail(String email){

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return modelMapper.map(member, MemberResponseDto.class);
    }

    public void delete(String id) {
        memberRepository.deleteById(id);
    }
}