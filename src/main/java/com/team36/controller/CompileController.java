package com.team36.controller;

import com.team36.builder.CompileBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
public class CompileController {

    @Autowired
    private CompileBuilder compileBuilder;

    @PostMapping("/compile")
    public Map<String, Object> compileAndRun(@RequestBody Map<String, String> request, Principal principal) {
        String code = request.get("code");
        String fileName = request.get("fileName");
        LocalDateTime dateTime = LocalDateTime.now();
        String output;
        String mid = principal.getName();

        try {
            output = compileBuilder.compileAndRunCode(code,fileName,mid);

        } catch (Exception e) {
            output = "Error during execution: " + e.getMessage();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("output", output);
        response.put("date", dateTime.format(DateTimeFormatter.ofPattern("MM/dd")));
        response.put("time", dateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        return response;
    }
}
