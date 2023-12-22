package com.team36.controller;

import com.team36.domain.Notice;
import com.team36.dto.NoticeDTO;
import com.team36.service.NoticeService;
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
    public String getList(Model model){

        List<NoticeDTO> list = noticeService.list();
        model.addAttribute("notice", list);

        return "notice/noticeList";
    }

    @GetMapping("/notice/detail")
    public String noticeDetail(@RequestParam("no") Integer no, Model model) {
        NoticeDTO noticeDTO = noticeService.detail(no);
        model.addAttribute("detail", noticeDTO);

        return "notice/noticeDetail";
    }

}
