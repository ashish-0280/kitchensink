package com.example.kitchensink.service;

import com.example.kitchensink.dto.MemberRequestDto;
import com.example.kitchensink.dto.MemberResponseDto;
import com.example.kitchensink.exception.ResourceNotFoundException;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    public MemberService(MemberRepository memberRepository, ModelMapper modelMapper) {
        this.memberRepository = memberRepository;
        this.modelMapper = modelMapper;
    }

    public Page<MemberResponseDto> searchMembers(String keyword, Pageable pageable){

        Page<Member> members;

        if(keyword == null || keyword.isBlank()){
            members = memberRepository.findAll(pageable);
        }
        else{
            members = memberRepository.searchMembers(keyword,pageable);
        }

        return members.map(member ->
                modelMapper.map(member,MemberResponseDto.class)
        );
    }

    public MemberResponseDto getById(String id){

        Member member = memberRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Member not found with id: "+id)
                );

        return modelMapper.map(member,MemberResponseDto.class);
    }

    public MemberResponseDto getByEmail(String email){

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with email: "+email)
                );

        return modelMapper.map(member,MemberResponseDto.class);
    }

    public MemberResponseDto update(String id, MemberRequestDto dto){

        Member member = memberRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Member not found"));

        member.setName(dto.getName());
        member.setEmail(dto.getEmail());
        member.setPhone(dto.getPhone());

        Member updated = memberRepository.save(member);

        return modelMapper.map(updated, MemberResponseDto.class);
    }

    public void delete(String id){
        memberRepository.deleteById(id);
    }

}