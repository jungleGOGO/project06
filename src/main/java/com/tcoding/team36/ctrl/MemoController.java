package com.tcoding.team36.ctrl;

import com.tcoding.team36.domain.Memo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.FileOutputStream;
import java.io.OutputStream;

@Controller
@Slf4j
@RequestMapping("/api")
public class MemoController {

    @PostMapping("/test1")
    @ResponseBody
    public String setFile(@RequestBody Memo memo) throws Exception {
        String filename = memo.getFilename();
        String monaco = memo.getMonaco();
//        OutputStream file = new FileOutputStream("/Users/juncheol/Desktop/java/"+filename); //
//        OutputStream file = new FileOutputStream("D:\\hk\\project\\file\\"+filename); //
        OutputStream file = new FileOutputStream("D:\\hk\\project\\file\\"+filename);
        byte[] bt = monaco.getBytes(); //OutputStream은 바이트 단위로 저장됨
        file.write(bt);
        file.close();
        log.info("filename : "+filename);
        log.info("conetenz : "+monaco);
        return filename;
    }
}
