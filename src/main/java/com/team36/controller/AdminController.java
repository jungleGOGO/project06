package com.team36.controller;

import com.team36.domain.Member;
import com.team36.dto.MemberJoinDTO;
import com.team36.dto.MemberSecurityDTO;
import com.team36.dto.PageDTO;
import com.team36.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
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
    public String adminHome(Model model, HttpServletRequest request) {
        log.info("---------------------- ADMIN ----------------------");
        PageDTO<Member, MemberJoinDTO> pageDTO = new PageDTO<>();

        String type = request.getParameter("type");
        String keyword = request.getParameter("keyword");

        int pageCurrent = request.getParameter("page") != null ? Integer.parseInt(request.getParameter("page")) : 1;
        pageDTO.setType(type);
        pageDTO.setKeyword(keyword);
        pageDTO.setPageCurrent(pageCurrent);

        pageDTO = memberService.memberList(pageDTO);

        List<MemberJoinDTO> list = pageDTO.getPagindDTO();
        model.addAttribute("list", list);
        model.addAttribute("pageDTO", pageDTO);


        return "admin/dashboard";
    }

    @PostMapping("/admin/activeUpdate")
    @ResponseBody
    public boolean activeUpdatePro(@RequestParam("active") Integer active, @RequestParam("mid") Integer mid){

        memberService.changeActive(active, mid);
        return true;
    }


}