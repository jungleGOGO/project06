package com.tcoding.team36.ctrl;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home(Model model) throws Exception {

        return "index"; // Thymeleaf 템플릿 파일의 이름을 반환합니다.
    }

    @GetMapping("/fileList")
    @ResponseBody
    public List<String> fileList(Model model) throws Exception {
        String directoryPath = "D:\\hk\\project\\file";
        List<String> files = new ArrayList<>();

        try (Stream<Path> walk = Files.walk(Paths.get(directoryPath))) {
            files = walk.filter(Files::isRegularFile)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            // 예외 처리: 파일 목록을 가져오는 도중에 오류가 발생하면 적절히 처리합니다.
        }

        return files;
    }
}
