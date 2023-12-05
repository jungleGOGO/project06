package com.team36.controller;

import com.team36.domain.Member;
import com.team36.dto.MemberJoinDTO;
import com.team36.repository.MemberRepository;
import com.team36.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.internal.Errors;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @GetMapping("/join")
    public String joinGET(Model model){
        log.info("join get...");
        model.addAttribute("memberJoinDTO", new MemberJoinDTO());
        return "member/join";
    }

    @PostMapping("/join")
    public String joinPOST(@Valid MemberJoinDTO memberJoinDTO, BindingResult bindingResult, Model model){
        log.info("join post...");
        log.info(memberJoinDTO);

        String email = memberJoinDTO.getEmail();
        Member existEmail = memberService.existByEmail(email);
        System.out.println(existEmail);
        if(existEmail!=null){
            bindingResult
                    .rejectValue("email", "error.email", "사용이 불가한 이메일입니다.");
        }

        if(bindingResult.hasErrors()){
          model.addAttribute("memberJoinDTO", memberJoinDTO);
        return "member/join";
        }
        memberService.join(memberJoinDTO);
        return "redirect:/";

    }

    @GetMapping("/login")
    public String loginGet(){
        log.info("/login get ...........");
        return "member/login";
    }
    @GetMapping("/mypage")
    public String mypage(){
        return "member/mypage";
    }
}