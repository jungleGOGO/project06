package com.team36.controller;

import com.team36.service.EmailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class EmailController {
    @Autowired
    EmailServiceImpl emailService;

    @PostMapping("/emailConfirm")
    @ResponseBody
    public String emailConfirm(@RequestParam String email, Model model) throws Exception{
        String confirm = emailService.sendSimpleMessage(email);
        model.addAttribute("code",confirm);
        return confirm;
    }

    @PostMapping("/verifyCode")
    @ResponseBody
    public int verifyCode(@RequestParam("code") String code) {
        System.out.println("리다이렉트?");
        int result = 0;
        String storedCode = emailService.getVerificationCode();
        System.out.println("code : " + code);
        System.out.println("code match : " + storedCode.equals(code));
        if (storedCode.equals(code)) {
            result = 1;
        }
        return result;
    }
}
