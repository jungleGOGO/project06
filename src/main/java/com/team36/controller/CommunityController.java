package com.team36.controller;

import com.team36.domain.Community;
import com.team36.domain.Member;
import com.team36.dto.CommunityDTO;
import com.team36.dto.PageDTO;
import com.team36.repository.MemberRepository;
import com.team36.service.CommunityService;
import com.team36.service.MemberService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@Log4j2
@RequestMapping("/community")
public class CommunityController {

    @Autowired
    private CommunityService communityService;

    @Autowired
    private MemberRepository memberRepository;

    @GetMapping("/list")
    public String CommunityList(Model model, HttpServletRequest request){
        PageDTO<Community, CommunityDTO> pageDTO = new PageDTO<>();
        int pageCurrent = request.getParameter("page") != null ? Integer.parseInt(request.getParameter("page")) : 1;
        pageDTO.setPageCurrent(pageCurrent);

        pageDTO = communityService.communityList(pageDTO);
        List<CommunityDTO> communityList = pageDTO.getPagindDTO();
        model.addAttribute("communityList", communityList);
        System.out.println(communityList);
        model.addAttribute("pageDTO", pageDTO);


        return "community/communityList";
    }

    @GetMapping("/insert")
    public String CommunityInsertForm (Model model){
        log.info("----insert Start");

        model.addAttribute("communityDTO", new CommunityDTO());
        return "community/communityInsert";
    }

    @PostMapping("/insert")
    public String CommunityInsert(@Valid CommunityDTO communityDTO, BindingResult bindingResult, Principal principal, Model model) {
        log.info(communityDTO);


        if(bindingResult.hasErrors()){
            model.addAttribute("communityDTO", communityDTO);
        }
        String sid = principal.getName();
        Member member = memberRepository.findByMid(sid);
        String id = member.getMname();
        communityDTO.setAuthor(id);
        communityService.communityAdd(communityDTO);
        return "redirect:/community/list";

    }

    @GetMapping("/detail")
    public String CommunityDetail(@RequestParam("cno")Long cno, Model model){
        CommunityDTO communityDTO = communityService.detail(cno);
        log.info(communityDTO);
        model.addAttribute("detail", communityDTO);
        return "community/communityDetail";
    }


}