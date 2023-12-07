package com.team36.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Log4j2
@Controller
@RequestMapping("/admin")
public class AdminController {

    /*@PreAuthorize("hasRole('ADMIN')")*/
    @GetMapping("/home")
    public String adminHome(Model model) {
        log.info("---------------------- ADMIN ----------------------");
        return "admin/home";
    }

}