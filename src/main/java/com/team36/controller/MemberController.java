package com.team36.controller;

import com.team36.constant.MemberRole;
import com.team36.domain.Member;
import com.team36.domain.Profile;
import com.team36.dto.MemberJoinDTO;
import com.team36.dto.ProfileDTO;
import com.team36.repository.MemberRepository;
import com.team36.repository.ProfileRepository;
import com.team36.service.EmailService;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Log4j2
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final ProfileService profileService;
    private final EmailService emailService;

    @Autowired
    private BCryptPasswordEncoder passEncoder;

    @GetMapping("/login")
    public String login(Model model){
        model.addAttribute("memberJoinDTO", new MemberJoinDTO());
        return "member/login";
    }

    @GetMapping("/member/loginFail")
    public String loginFail (Model model) {
        model.addAttribute("msg", "로그인 실패! 다시 시도해 주세요:)");
        model.addAttribute("url", "/login");
        return "layout/alert";
    }
    @PostMapping("/join")
    public String joinPOST(@Valid MemberJoinDTO memberJoinDTO, BindingResult bindingResult, Model model){
        String email = memberJoinDTO.getEmail();
        Member existEmail = memberService.existByEmail(email);
        if(existEmail!=null){
            bindingResult
                    .rejectValue("email", "error.email", "사용이 불가한 이메일입니다.");
        }

        if(!Objects.equals(memberJoinDTO.getPasswordConfirm(), memberJoinDTO.getMpw())){
            bindingResult.rejectValue("passwordConfirm", "error.passwordConfirm", "비밀번호와 비밀번호 확인이 다릅니다.");
        }

        if(bindingResult.hasErrors()){
          model.addAttribute("error", bindingResult.hasErrors());
          model.addAttribute("memberJoinDTO", memberJoinDTO);
          return "member/login";
        }

        memberService.join(memberJoinDTO);
        model.addAttribute("msg", "회원가입이 정상적으로 처리되었습니다:)");
        model.addAttribute("url", "/login");
        return "layout/alert";
    }

    @GetMapping("/member/mypage")
    public String mypageForm(Principal principal, Model model){

        String mid = principal.getName();
        Member member =memberRepository.findByMid(mid);
        Profile profile = profileService.existsProfileByMember(member);
        model.addAttribute("member", member);
        model.addAttribute("profile", profile);
        return "member/mypage";
    }


    @PostMapping("/member/mypage")
    public String mypage (Profile profile, Principal principal){

        int mid = Integer.parseInt(principal.getName());
        Member member = memberRepository.findByMid(principal.getName());
        Profile exist = profileService.existsProfileByMember(member);
        ProfileDTO pf = new ProfileDTO();


        if(exist == null) {
            log.info("==============추가");
            pf.setMid(mid);
            pf.setMember(member);
            pf.setIntro(profile.getIntro());
            pf.setGitLink1(profile.getGitLink1());
            if (profile.getGitLink2() != null) {
                pf.setGitLink2(profile.getGitLink2());
            }
            profileService.insertProfile(pf);
        }else {
            log.info("==============수정");
            pf.setPno((long) exist.getPno());
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
        Member member = memberRepository.findByMid(principal.getName());
        String pw = member.getMpw();

        if(!Objects.equals(memberJoinDTO.getPasswordConfirm(), memberJoinDTO.getMpw())) {
            bindingResult.rejectValue("passwordConfirm", "error.passwordConfirm", "비밀번호와 비밀번호 확인이 다릅니다.");
        }

        if(!passEncoder.matches(memberJoinDTO.getNowPassword(), pw)) {
            bindingResult.rejectValue("nowPassword", "error.nowPassword", "비밀번호가 잘못되었습니다.");
        }

        MemberJoinDTO mem = new MemberJoinDTO();
        mem.setMid(Integer.parseInt(principal.getName()));
        mem.setMpw(memberJoinDTO.getMpw());
        memberService.changePw(mem);
        model.addAttribute("msg", "비밀번호가 정상적으로 변경되었습니다:)");
        model.addAttribute("url", "/member/mypage");

        return "layout/alert";
    }

    @PostMapping ("/member/changeName")
    @ResponseBody
    public boolean changName(@RequestParam("mname")String mname, Principal principal){
        MemberJoinDTO member = new MemberJoinDTO();
        member.setMid(Integer.parseInt(principal.getName()));
        member.setMname(mname);
        memberService.changeName(member);
        return true;
    }

    @PostMapping("/member/changeImage")
    @ResponseBody
    public boolean changeImage(@RequestParam("memberImg") String memberImg, Principal principal) throws FileNotFoundException {
        OutputStream file = new FileOutputStream("D:\\hk\\project\\img\\"+memberImg);
        return true;
    }

    @GetMapping("/member/changeActive")
    public String changActive(@RequestParam("mid")Integer mid, Principal principal, Model model) {
        log.info("======================들어왔는지");
        memberService.changeActive(2, mid);
        model.addAttribute("msg", "탈퇴되었습니다.안녕히가세요!");
        model.addAttribute("url", "/logout");
        return "layout/alert";
    }

    //비밀번호 찾기
    @PostMapping("/findPw")
    @ResponseBody
    public String findPassword(@RequestParam("email") String email, Model model) {
        System.out.println("받은 이메일주소"+email);
        Member mem = memberRepository.findByEmail(email);
        System.out.println("찾았는지"+mem.getEmail());
        MemberJoinDTO member = new MemberJoinDTO();
        String result = "fail";
        System.out.println();
        if(email.equals(mem.getEmail())){
            System.out.println("===============================");
            //임시비밀번호 암호화해서 DB에 저장
            String tempPassword = emailService.getRamdomPassword(8);
            System.out.println("바귄비민ㄹ번호"+tempPassword);
            member.setMid(Integer.parseInt(String.valueOf(mem.getMid())));
            member.setMpw(tempPassword);
            memberService.changePw(member);
            System.out.println("==============바뀌었느지");

            //이메일 전송
            emailService.sendTempPasswordEmail(email, tempPassword);

            result="success";
            return result;
       } else {
            model.addAttribute("msg","정보가 일치하지 않습니다.");
            return result;
        }

    }

}