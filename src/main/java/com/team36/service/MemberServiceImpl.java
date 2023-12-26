package com.team36.service;

import com.team36.constant.MemberRole;
import com.team36.domain.Member;
import com.team36.dto.MemberJoinDTO;
import com.team36.dto.MemberSecurityDTO;
import com.team36.dto.PageDTO;
import com.team36.repository.MemberRepository;
import com.team36.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.Errors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final ModelMapper modelMapper;

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void join(MemberJoinDTO memberJoinDTO) {

        Member member = modelMapper.map(memberJoinDTO, Member.class);
        member.changePassword(passwordEncoder.encode(memberJoinDTO.getMpw()));
        member.addRole(MemberRole.USER);
        member.changeActive(0,member.getMid());
        memberRepository.save(member);
    }

    @Override
    public Member existByEmail(String email) {
        return memberRepository.existsMemberByEmail(email);
    }

    @Override
    public void changePw(MemberJoinDTO memberJoinDTO) {
        Optional<Member> result = memberRepository.findById(String.valueOf(memberJoinDTO.getMid()));
        Member member = result.orElseThrow();
        System.out.println("============암호화전:"+memberJoinDTO.getMpw());
        System.out.println("===========암호화후"+passwordEncoder.encode(memberJoinDTO.getMpw()));
        member.changePassword(passwordEncoder.encode(memberJoinDTO.getMpw()));
        memberRepository.save(member);
    }

    @Override
    public boolean changeName(MemberJoinDTO memberJoinDTO) {
        Optional<Member> result = memberRepository.findById(String.valueOf(memberJoinDTO.getMid()));
        Member member = result.orElseThrow();
        member.changeMname(memberJoinDTO.getMname());
        memberRepository.save(member);
        return true;
    }

    @Override
    public boolean changeActive(Integer active, Integer mid) {
        Optional<Member> result = memberRepository.findById(String.valueOf(mid));
        Member member = result.orElseThrow();
        member.changeActive(active, mid);
        memberRepository.save(member);
        return true;
    }

    @Override
    public PageDTO<Member, MemberJoinDTO> memberList(PageDTO<Member, MemberJoinDTO> pageDTO) {
        Pageable pageable = pageDTO.getPageable();
        Page<Member> result = memberRepository.searchPage(pageable, pageDTO);
        pageDTO.build(result);
        pageDTO.entity2dto(result, MemberJoinDTO.class);
        return pageDTO;
    }
}