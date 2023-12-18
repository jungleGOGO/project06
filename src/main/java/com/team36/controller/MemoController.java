package com.team36.controller;

import com.team36.domain.Directory;
import com.team36.domain.Memo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.Principal;

@Controller
@Slf4j
@RequestMapping("/api")
public class MemoController {

    // 파일 저장
    @PostMapping("/test1")
    @ResponseBody
    public String setFile(@RequestBody Memo memo, Principal principal) throws Exception {
        String filename = memo.getFilename();
        String monaco = memo.getMonaco();

        String mid = principal.getName();

        OutputStream file = new FileOutputStream("/Users/juncheol/mounttest/"+mid+"/"+filename); //
//        OutputStream file = new FileOutputStream("\\\\10.41.0.153\\storage\\user1\\"+filename); //

        byte[] bt = monaco.getBytes(); //OutputStream은 바이트 단위로 저장됨
        file.write(bt);
        file.close();
        log.info("filename : "+filename);
        log.info("content : "+monaco);
        return filename;
    }

    //파일 내용 읽기
    @PostMapping("/test2")
    @ResponseBody
    public ResponseEntity<?> getFile(@RequestParam("filename2") String filename2) {

        String filePath = "/Users/juncheol/Desktop/storage" + filename2;
//        String filePath = "\\\\Y:\\storage" + filename2

        // 첫번째거 : 새파일(모달창) 만들기 경로 || 두번째거 : 트리에서 파일 불러오는 경로 (현경)
        // String filePath  = "\\\\10.41.0.153\\storage\\user1\\"+ filename2;
//        String filePath = "\\\\10.41.0.153\\storage" + filename2;

        File file = new File(filePath);
        Path path = Path.of(filePath);

        // 파일이 존재하고, 실제 파일인지 확인
        if (!file.exists() || !file.isFile()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("파일을 찾을 수 없거나 디렉토리입니다: " + filename2);
        }

        try {
            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
            System.out.println("크기 : "+attrs.size());
            System.out.println("생성일 : "+attrs.creationTime());
            System.out.println("수정일 : "+attrs.lastModifiedTime());

            String fileContent = readFile(filePath);
            System.out.println("fileContent : " + fileContent);
            System.out.println("filePath  : " + filePath);
            log.info("filename : " + filename2);
            return ResponseEntity.ok(fileContent);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("파일 읽기 오류: " + e.getMessage());
        }
    }

    // 파일 내용을 읽어오는 메서드
    private String readFile(String filePath) throws IOException {
        File file = new File(filePath);

        // 파일이 존재하고, 실제 파일인지 확인
        if (!file.exists() || !file.isFile()) {
            throw new FileNotFoundException("파일을 찾을 수 없거나 디렉토리입니다: " + filePath);
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        }
    }


    //폴더 생성
    @PostMapping("/mkdir")
    public ResponseEntity<?>  createDirectory(@RequestBody Directory directory) {

        String webPath = directory.getPath(); // 웹 경로 (/user1/dir1 형식)
        String mkdirname = directory.getMkdirname(); // 생성할 디렉토리 이름

        if (mkdirname.contains("..") || mkdirname.contains("/") || mkdirname.contains("\\") ||
                mkdirname.contains(":") || mkdirname.contains("*") || mkdirname.contains("?") ||
                mkdirname.contains("\"") || mkdirname.contains("<") || mkdirname.contains(">") ||
                mkdirname.contains("|") || mkdirname.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 폴더명입니다.");
        }


        // 웹 경로를 파일 시스템 경로로 변환
        String baseDir = "/Users/juncheol/Desktop/storage"; // 기본 경로
//        String baseDir = "\\\\Y:\\storage";
        String filePath = baseDir + webPath.replace("/", File.separator);


        Path directoryPath;

        File file = new File(filePath);
        if (file.isDirectory()) {
            // 디렉토리인 경우
            directoryPath = Paths.get(filePath, mkdirname);
        } else {
            // 파일인 경우
            directoryPath = file.toPath().getParent().resolve(mkdirname);
        }
        System.out.println("directoryPath : "+directoryPath);
        try {
            Files.createDirectories(directoryPath);
            return ResponseEntity.ok("폴더 생성 완료: " + directoryPath.toString());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("폴더 생성 실패: " + e.getMessage());
        }
    }

    // 새 파일
    @PostMapping("/newFile")
    @ResponseBody
    public String setNewFile(@RequestBody Memo memo, Principal principal) throws Exception {
        String filename = memo.getFilename();
        String monaco = memo.getMonaco();

        String mid = principal.getName();

        OutputStream file = new FileOutputStream("/Users/juncheol/Desktop/storage/"+mid+"/"+filename); //
//        OutputStream file = new FileOutputStream("\\\\10.41.0.153\\storage\\user1\\"+filename); //

        byte[] bt = monaco.getBytes(); //OutputStream은 바이트 단위로 저장됨
        file.write(bt);
        file.close();
        log.info("filename : "+filename);
        log.info("content : "+monaco);
        return filename;
    }
}
