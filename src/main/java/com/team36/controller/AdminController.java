package com.team36.controller;

import com.team36.domain.Member;
import com.team36.dto.MemberJoinDTO;
import com.team36.dto.MemberSecurityDTO;
import com.team36.dto.NoticeDTO;
import com.team36.dto.PageDTO;
import com.team36.service.MemberService;
import com.team36.service.NoticeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
public class AdminController {

    private final MemberService memberService;
    private final NoticeService noticeService;

    /*@PreAuthorize("hasRole('ADMIN')")*/
    @GetMapping("/admin/dash")
    public String adminHome(Model model, HttpServletRequest request) {
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

    @GetMapping("/admin/noticeList")
    public String getList(Model model){

        List<NoticeDTO> list = noticeService.list();
        model.addAttribute("list", list);

        return "admin/noticeListAdmin";
    }
    @GetMapping("/admin/noticeInsert")
    public String getNoticeInsertForm(){
        return "admin/noticeInsert";
    }

    @PostMapping("/admin/noticeInsert")
    public String NoticeInsert(@Valid NoticeDTO noticeDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes){

        if(bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors() );
        }

        noticeService.insert(noticeDTO);

        return "redirect:/admin/noticeList";

    }
    @GetMapping("/admin/noticeEdit")
    public String getNoticeEditForm(@RequestParam("no") Integer no, Model model) {
        NoticeDTO noticeDTO = noticeService.detail(no);
        model.addAttribute("notice", noticeDTO);

        return "admin/noticeEdit";
    }

    @PostMapping("/admin/noticeEdit")
    public String NoticeEdit(@Valid NoticeDTO noticeDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors() );
        }


        noticeService.edit(noticeDTO);
        return "redirect:/admin/noticeList";
    }

    @GetMapping("/admin/noticeDelete")
    public String boardRemove(Integer no, RedirectAttributes redirectAttributes) {
        noticeService.delete(no);

        return "redirect:/admin/noticeList";
    }

}