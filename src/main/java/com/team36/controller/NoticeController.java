package com.team36.controller;

import com.team36.domain.Notice;
import com.team36.dto.NoticeDTO;
import com.team36.dto.PageDTO;
import com.team36.service.NoticeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/notice/list")
    public String getList(Model model, HttpServletRequest request){

        PageDTO<Notice, NoticeDTO> pageDTO = new PageDTO<>();

        String type = request.getParameter("type");
        String keyword = request.getParameter("keyword");

        int pageCurrent = request.getParameter("page") != null ? Integer.parseInt(request.getParameter("page")) : 1;
        pageDTO.setType(type);
        pageDTO.setKeyword(keyword);
        pageDTO.setPageCurrent(pageCurrent);

        pageDTO = noticeService.noticeList(pageDTO);

        List<NoticeDTO> list = pageDTO.getPagindDTO();
        model.addAttribute("list", list);
        model.addAttribute("pageDTO", pageDTO);


        return "notice/noticeList";
    }

    @GetMapping("/notice/detail")
    public String noticeDetail(@RequestParam("no") Integer no, Model model) {
        NoticeDTO noticeDTO = noticeService.detail(no);
        model.addAttribute("detail", noticeDTO);

        return "notice/noticeDetail";
    }

}
