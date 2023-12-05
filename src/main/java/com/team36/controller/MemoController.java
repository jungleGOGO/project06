package com.team36.controller;

import com.team36.domain.Memo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.*;

@Controller
@Slf4j
@RequestMapping("/api")
public class MemoController {

    @PostMapping("/test1")
    @ResponseBody
    public String setFile(@RequestBody Memo memo) throws Exception {
        String filename = memo.getFilename();
        String monaco = memo.getMonaco();
        OutputStream file = new FileOutputStream("/Users/juncheol/Desktop/storage/user1/"+filename); //
//        OutputStream file = new FileOutputStream("D:\\hk\\project\\file\\"+filename); //
//        OutputStream file = new FileOutputStream("D:\\hk\\project\\file\\"+filename);
        byte[] bt = monaco.getBytes(); //OutputStream은 바이트 단위로 저장됨
        file.write(bt);
        file.close();
        log.info("filename : "+filename);
        log.info("content : "+monaco);
        return filename;
    }

    @PostMapping("/test2")
    @ResponseBody
    public String getFile(@RequestParam("filename2") String filename2) throws IOException {
        // 파일 경로
        String filePath = "/Users/juncheol/Desktop/storage" + filename2;
//        String filePath = "D:\\hk\\project\\file\\" + filename2;

        // 파일 내용을 읽어오는 메서드 호출
        String fileContent = readFile(filePath);
        System.out.println("fileContent : "+fileContent);
        System.out.println("filePath  : "+filePath);
        log.info("filename : " + filename2);
        return fileContent;
    }

    // 파일 내용을 읽어오는 메서드
    private String readFile(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        }
    }
}
