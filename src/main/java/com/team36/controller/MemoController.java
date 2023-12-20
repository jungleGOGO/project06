package com.team36.controller;

import com.team36.domain.Directory;
import com.team36.domain.Memo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.Principal;
import java.util.Set;
import java.util.stream.Stream;
import java.util.Map;

@Controller
@Slf4j
@RequestMapping("/api")
public class MemoController {

    // 파일 생성 버튼 (btn1)
    @PostMapping("/test1")
    @ResponseBody
    public ResponseEntity<?> setFile(@RequestBody Memo memo, Principal principal) throws Exception {
        String filename = memo.getFilename();
//        String monaco = memo.getMonaco();
        int dotIndex = filename.lastIndexOf(".");
        String noExtensionFileName = filename.substring(0, dotIndex); // 파일 확장자 잘라내기
        String mid = principal.getName();

        String code = "public class "+noExtensionFileName+" {\n"+
                "\tpublic static void main(String[] args) {\n" +
                "\t\t\n"+
                "\t}\n"+
                "}";

        String webPath = memo.getPath(); // 웹 경로 (/user1/dir1 형식)
        // 웹 경로를 파일 시스템 경로로 변환
        // TODO : 경로 수정
//        String baseDir = "/Users/juncheol/mounttest/"; // 기본 경로
//        String baseDir = "\\\\Y:\\storage";
        String baseDir = "\\\\10.41.0.153\\storage";
        String filePath = baseDir + webPath.replace("/", File.separator);

        System.out.println("chk filePath : " + baseDir+mid);

        long count=0;
        try (Stream<Path> files = Files.list(Paths.get(baseDir+mid))) {
            count = files.count();
            System.out.println("파일/디렉토리 개수: " + count);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 파일/폴더 50개 제한
        if (count>45){
            return ResponseEntity.status(HttpStatus.INSUFFICIENT_STORAGE) // 507 에러 코드 반환(서버 용량 부족 에러 코드)
                    .body("파일 생성 실패: 더 이상 파일 및 폴더를 생성할 수 없습니다.");
        }



        Path directoryPath;

        File file = new File(filePath);
        if (file.isDirectory()) {
            // 디렉토리인 경우
            directoryPath = Paths.get(filePath, filename);
        } else {
            // 파일인 경우
            directoryPath = file.toPath().getParent().resolve(filename);
        }
        System.out.println("directoryPath : "+directoryPath);

        if (Files.exists(directoryPath)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("파일 생성 실패: 파일이 이미 존재합니다.");
        }

//      기존 파일 생성 처리 부분
//        try {
////            Files.createDirectories(directoryPath);
//            OutputStream newFile = new FileOutputStream(directoryPath.toString());
//            byte[] bt = code.getBytes(); //OutputStream은 바이트 단위로 저장됨
//            newFile.write(bt);
//            newFile.close();
//            return ResponseEntity.ok("파일 생성 완료:" + webPath+"/"+filename);
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("파일 생성 실패: " + e.getMessage());
//        }

        //파일 생성시 권한 777 부여
        try {
            Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxrwxrwx");
            FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);

            Path filePermission = Files.createFile(directoryPath, attr);

//            Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxrwxrwx");
            Files.setPosixFilePermissions(filePermission, perms);


            OutputStream newFile = new FileOutputStream(filePermission.toFile());
            byte[] bt = code.getBytes(); //OutputStream은 바이트 단위로 저장됨
            newFile.write(bt);


            newFile.close();
            return ResponseEntity.ok("파일 생성 완료:" + webPath+"/"+filename);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("파일 생성 실패: " + e.getMessage());
        }

    }

    //파일 내용 읽기
    @PostMapping("/readFile")
    @ResponseBody
    public ResponseEntity<?> getFile(@RequestParam("filename2") String filename2) {

        // TODO : 경로 수정
//        String filePath = "/Users/juncheol/Desktop/storage" + filename2;
        String filePath = "/Users/juncheol/mounttest" + filename2;
//        String filePath = "\\\\Y:\\storage" + filename2;
//        String filePath = "\\\\10.41.0.153\\storage" +filename2;


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
    public ResponseEntity<?>  createDirectory(@RequestBody Directory directory, Principal principal) {

        String webPath = directory.getPath(); // 웹 경로 (/user1/dir1 형식)
        String mkdirname = directory.getMkdirname(); // 생성할 디렉토리 이름
        String mid = principal.getName();

        if (mkdirname.contains("..") || mkdirname.contains("/") || mkdirname.contains("\\") ||
                mkdirname.contains(":") || mkdirname.contains("*") || mkdirname.contains("?") ||
                mkdirname.contains("\"") || mkdirname.contains("<") || mkdirname.contains(">") ||
                mkdirname.contains("|") || mkdirname.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 폴더명입니다.");
        }

        // TODO : 경로 수정
        // 웹 경로를 파일 시스템 경로로 변환
//        String baseDir = "/Users/juncheol/mounttest"; // 기본 경로
//        String baseDir = "/Users/juncheol/Desktop/storage"; // 기본 경로
//        String baseDir = "\\\\Y:\\storage";
        String baseDir = "\\\\10.41.0.153\\storage";
        String filePath = baseDir + webPath.replace("/", File.separator);

        long count=0;
        try (Stream<Path> files = Files.list(Paths.get(baseDir+mid))) {
            count = files.count();
            System.out.println("파일/디렉토리 개수: " + count);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 파일/폴더 50개 제한
        if (count>50){
            return ResponseEntity.status(HttpStatus.INSUFFICIENT_STORAGE) // 507 에러 코드 반환(서버 용량 부족 에러 코드)
                    .body("파일 생성 실패: 더 이상 파일 및 폴더를 생성할 수 없습니다.");
        }



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
        // TODO : 경로 수정
//        OutputStream file = new FileOutputStream("/Users/juncheol/Desktop/storage/"+mid+"/"+filename);
        OutputStream file = new FileOutputStream("/Users/juncheol/mounttest/"+mid+"/"+filename); //
//        OutputStream file = new FileOutputStream("\\\\10.41.0.153\\storage\\user1\\"+filename); //

        byte[] bt = monaco.getBytes(); //OutputStream은 바이트 단위로 저장됨
        file.write(bt);
        file.close();
        log.info("filename : "+filename);
        log.info("content : "+monaco);
        return filename;
    }

    @PostMapping("/deleteFile")
    public String deleteJavaFile(@RequestBody Map<String, String> payload) throws Exception {
        String filename = payload.get("filename");
        String rootDirectoryPath = "\\\\10.41.0.153\\storage";
        String filePath = rootDirectoryPath +filename;
        File fileToDelete = new File(filePath);

        if (fileToDelete.exists()) {
            // 디렉토리인 경우 내용물을 먼저 삭제
            if (fileToDelete.isDirectory()) {
                FileSystemUtils.deleteRecursively(fileToDelete);
            } else {
                // 파일인 경우 바로 삭제
                fileToDelete.delete();
            }
            System.out.println("삭제 성공: " + filePath);
        } else {
            System.out.println("삭제할 파일 또는 폴더가 존재하지 않습니다: " + filePath);
            //
        }
        return "redirect:/java/project";
    }

    @PostMapping("/rename")
    public ResponseEntity<String> renameFile(
            @RequestParam("currentFilename") String currentFilename,
            @RequestParam("newFilename") String newFilename,
            @RequestParam("currentFolder") String currentFolder,
            Model model, Principal principal) {
        System.out.println("현재 파일 이름: " + currentFilename);
        System.out.println("바꿀 파일 이름: " + newFilename);
        System.out.println("현재 디렉토리: " + currentFolder);

        String mid = principal.getName();
        String rootDirectoryPath = "\\\\10.41.0.153\\storage";

        String filePath = rootDirectoryPath + currentFolder + newFilename;
        Path file = Paths.get(rootDirectoryPath + currentFolder + currentFilename);
        Path newFile = Paths.get(filePath);

        if (Files.exists(newFile)) {
            String msg = "해당 파일명으로 저장하실 수 없습니다.(파일명 중복)";
            model.addAttribute("msg", msg);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }

        try {
            Path newFilePath = Files.move(file, newFile, StandardCopyOption.REPLACE_EXISTING);
            System.out.println(newFilePath);
        } catch (IOException e) {
            e.printStackTrace();
            // 파일 이동 중 에러가 발생한 경우 에러 응답 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 이동 중 에러 발생");
        }

        return ResponseEntity.ok("파일이 성공적으로 이동되었습니다");
    }
}
