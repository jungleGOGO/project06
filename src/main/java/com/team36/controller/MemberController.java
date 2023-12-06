package com.team36.controller;

import com.team36.domain.Member;
import com.team36.domain.Profile;
import com.team36.dto.MemberJoinDTO;
import com.team36.dto.ProfileDTO;
import com.team36.repository.MemberRepository;
import com.team36.repository.ProfileRepository;
import com.team36.service.MemberService;
import com.team36.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.internal.Errors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final ProfileService profileService;

    @Autowired
    private BCryptPasswordEncoder passEncoder;

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

        if(memberJoinDTO.getPasswordConfirm() != memberJoinDTO.getMpw())
            bindingResult.rejectValue("passwordConfirm", "error.passwordConfirm", "비밀번호와 비밀번호 확인이 다릅니다.");


        if(bindingResult.hasErrors()){
          model.addAttribute("memberJoinDTO", memberJoinDTO);
        return "member/join";
        }
        memberService.join(memberJoinDTO);
        return "redirect:/";

    }

    @GetMapping("/member/mypage")
    public String mypageForm(Principal principal, Model model){
        log.info("/mypage .........");

        String mid = principal.getName();
        Member member =memberRepository.findByMid(mid);
        Profile profile = profileService.existsProfileByMember(member);
        System.out.println(profile);
        model.addAttribute("member", member);
        model.addAttribute("profile", profile);
        return "member/mypage";
    }

    @GetMapping("/login")
    public String login(){
        return "member/login";
    }

    @PostMapping("/member/mypage")
    public String mypage (Profile profile, Principal principal){

        String mid = principal.getName();
        Member member = memberRepository.findByMid(mid);
        Profile exist = profileService.existsProfileByMember(member);
        log.info(exist);
        Profile pf = new Profile();


        if(exist == null) {
            log.info("==============추가");
            pf.setMember(member);
            pf.setIntro(profile.getIntro());
            pf.setGitLink1(profile.getGitLink1());
            if (profile.getGitLink2() != null) {
                pf.setGitLink2(profile.getGitLink2());
            }
            profileService.insertProfile(pf);
        }else {
            log.info("==============수정");
            pf.setPno(exist.getPno());
            pf.setIntro(profile.getIntro());
            pf.setGitLink1(profile.getGitLink1());
            if (profile.getGitLink2() != null) {
                pf.setGitLink2(profile.getGitLink2());
            }
            profileService.updateProfile(pf);

        }

        return "redirect:/member/mypage";
    }

    @GetMapping("/member/check")
    public String pwCheckForm (){
        return "/member/pwCheck";
    }

    @PostMapping("/member/check")
    public String pwCheck(@RequestParam("mpw")String mpw, Principal principal, Model model){
        Member member = memberRepository.findByMid(principal.getName());
        String pw = member.getMpw();

        if(passEncoder.matches(mpw, pw)){
            return "redirect:/member/memberUpdate";
        } else{
            model.addAttribute("msg", "비밀번호가 틀렸습니다. 다시 작성해주세요:)");
            model.addAttribute("url", "/member/check");
            return "layout/alert";
        }
    }

    @GetMapping("/member/memberUpdate")
    public String memberUpdateForm(Principal principal, Model model){
        String mid = principal.getName();
        Member member = memberRepository.findByMid(mid);
        String pw = "";
        for(int i=0; i<member.getMpw().length(); i++){
            pw += "*";
        }
        model.addAttribute("pw", pw);
        model.addAttribute("member", member);
        return "/member/memberEdit";
    }

    @GetMapping("/member/changePw")
    public String changPwForm(Model model){
        model.addAttribute("memberJoinDTO", new MemberJoinDTO());
        return "/member/changePw";
    }

    @PostMapping("/member/changePw")
    public String changePw(@Valid MemberJoinDTO memberJoinDTO, BindingResult bindingResult,Principal principal, Model model){

        if(memberJoinDTO.getPasswordConfirm() != memberJoinDTO.getMpw())
            bindingResult.rejectValue("mpw", "error.mpw", "비밀번호와 비밀번호 확인이 다릅니다.");

        if(passEncoder.matches(memberJoinDTO.getNowPassword(), memberRepository.findByMid(principal.getName()).getMpw())){
            MemberJoinDTO mem = new MemberJoinDTO();
            mem.setMid(Integer.parseInt(principal.getName()));
            mem.setMpw(memberJoinDTO.getMpw());
            memberService.changePw(mem);
        }

        return "redirect:/member/mypage";
    }
}