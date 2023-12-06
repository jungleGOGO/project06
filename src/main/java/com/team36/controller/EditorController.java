package com.team36.controller;

import com.team36.domain.Editor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.FileOutputStream;
import java.io.OutputStream;

@Controller
@Log4j2
@RequestMapping("/editor")
public class EditorController {

    @PostMapping("/get")
    @ResponseBody
    public String setFile(@RequestBody Editor editor) throws Exception{
        String filename = editor.getFilename();
        String content = editor.getContent();

        OutputStream file = new FileOutputStream("D:\\hk\\project\\file\\"+filename);
        byte [] bt = content.getBytes();
        file.write(bt);
        file.close();
        log.info("filename : "+filename);
        log.info("content : "+content);
        return filename;

    }

}
