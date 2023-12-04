package com.team36.controller;

import com.team36.dto.CommunityDTO;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Log4j2
@RequestMapping("/request")
public class CommunityController {

    @GetMapping("/insert")
    public String CommunityInsertForm (){
        return "community/communityInsert";
    }

    @PostMapping("/insert")
    public String CommunityInsert(@Valid CommunityDTO communityDTO, BindingResult bindingResult) {

    }
}
