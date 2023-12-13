package com.team36.controller;

import com.team36.dto.MemberJoinDTO;
import com.team36.dto.MemberSecurityDTO;
import com.team36.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
public class AdminController {

    private final MemberService memberService;

    /*@PreAuthorize("hasRole('ADMIN')")*/
    @GetMapping("/admin/dash")
    public String adminHome(Model model) {
        log.info("---------------------- ADMIN ----------------------");
        List<MemberJoinDTO> list = memberService.list();
        model.addAttribute("list", list);
        System.out.println(list);
        return "admin/dashboard";
    }

    @PostMapping("/admin/activeUpdate")
    @ResponseBody
    public boolean activeUpdatePro(@RequestParam("active") Integer active, @RequestParam("mid") Integer mid){

        memberService.changeActive(active, mid);
        return true;
    }


}