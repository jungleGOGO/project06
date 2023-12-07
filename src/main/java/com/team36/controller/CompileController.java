package com.team36.controller;

import com.team36.builder.CompileBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
public class CompileController {
    @Autowired
    private CompileBuilder compileBuilder;

    @PostMapping("/compile")
    public Map<String, Object> compileAndRun(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        String output;
        try {
            output = compileBuilder.compileAndRunCode(code);
        } catch (Exception e) {
            output = "Error during execution: " + e.getMessage();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("output", output);
        return response;
    }
}