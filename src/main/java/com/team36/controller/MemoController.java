package com.team36.controller;

import com.team36.domain.Code;
import com.team36.domain.Directory;
import com.team36.domain.Memo;
import com.team36.util.CompressZip;
import com.team36.dto.ResponseEntityDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
        System.out.println(webPath);
        // 웹 경로를 파일 시스템 경로로 변환
        // TODO : 경로 수정
        String baseDir = "/Users/juncheol/mounttest/"+mid+"/java"; // 기본 경로
//        String baseDir = "\\\\10.41.0.153\\storage\\"+mid+"/java";
        String filePath = baseDir + webPath.replace("/", File.separator);
//        String filePath = baseDir + webPath.replace("/", File.separator);


//        System.out.println("(MemoController:58) :"+baseDir); //경로 확인

//        String baseDir = "/Users/juncheol/mounttest/"+mid+"/java"; // 기본 경로
//        String baseDir = "\\\\10.41.0.153\\storage\\"+mid+"\\java";
//        String filePath = baseDir + webPath.replace("\\", File.separator);
//        String filePath = baseDir + webPath.replace("/", File.separator);


        long count=0;
        try (Stream<Path> files = Files.list(Paths.get(baseDir))) {
            count = files.count();
            System.out.println("파일/디렉토리 개수: " + count);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 파일/폴더 30개 제한
        if (count>30){
            return ResponseEntity.status(HttpStatus.INSUFFICIENT_STORAGE) // 507 에러 코드 반환(서버 용량 부족 에러 코드)
                    .body("파일 생성 실패: 더 이상 파일 및 폴더를 생성할 수 없습니다.");
        }



        Path directoryPath;
        System.out.println("(MemoCtrl :76) filePath : "+filePath);
        File file = new File(filePath);
        if (file.isDirectory()) {
            // 디렉토리인 경우
            directoryPath = Paths.get(filePath, filename);
        } else {
            // 파일인 경우
            directoryPath = file.toPath().getParent().resolve(filename);
        }

        if (Files.exists(directoryPath)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("파일 생성 실패: 파일이 이미 존재합니다.");
        }

//      기존 파일 생성 처리 부분
        try {
//            Files.createDirectories(directoryPath);
            OutputStream newFile = new FileOutputStream(directoryPath.toString());
            byte[] bt = code.getBytes(); //OutputStream은 바이트 단위로 저장됨
            newFile.write(bt);
            newFile.close();
            return ResponseEntity.ok("파일 생성 완료:" + webPath+"/"+filename);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("파일 생성 실패: " + e.getMessage());
        }

        //파일 생성시 권한 777 부여
//        try {
//            Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxrwxrwx");
//            FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);
//
//            Path filePermission = Files.createFile(directoryPath, attr);
//
////            Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxrwxrwx");
//            Files.setPosixFilePermissions(filePermission, perms);
//
//
//            OutputStream newFile = new FileOutputStream(filePermission.toFile());
//            byte[] bt = code.getBytes(); //OutputStream은 바이트 단위로 저장됨
//            newFile.write(bt);
//
//
//            newFile.close();
//            return ResponseEntity.ok("파일 생성 완료:" + webPath+"/"+filename);
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("파일 생성 실패: " + e.getMessage());
//        }

    }

    //파일 내용 읽기
    @PostMapping("/readFile")
    @ResponseBody
    public ResponseEntity<?> getFile(@RequestParam("filename2") String filename2,Principal principal) {

        String mid = principal.getName();
        // TODO : 경로 수정
        String filePath = "/Users/juncheol/mounttest/" + mid+"/java"+filename2;
//        String filePath = "\\\\10.41.0.153\\storage\\" + mid+"\\java"+filename2;


        File file = new File(filePath);
        Path path = Path.of(filePath);

        // 파일이 존재하고, 실제 파일인지 확인
        if (!file.exists() || !file.isFile()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("파일을 찾을 수 없거나 디렉토리입니다: " + filename2);
        }

        try {
            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
            System.out.println("(MemoController:151) 생성일 : "+attrs.creationTime());
            System.out.println("(MemoController:152) 수정일 : "+attrs.lastModifiedTime());

            String fileContent = readFile(filePath);
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
        String baseDir = "/Users/juncheol/mounttest/"+mid+"/java"; // 기본 경로
//        String baseDir = "/Users/juncheol/Desktop/storage"; // 기본 경로
//        String baseDir = "\\\\10.41.0.153\\storage\\"+mid+"\\java";
        String filePath = baseDir + webPath.replace("/", File.separator);

        long count=0;
        try (Stream<Path> files = Files.list(Paths.get(baseDir))) {
            count = files.count();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 파일/폴더 30개 제한
        if (count>30){
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
        if (Files.exists(directoryPath)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("폴더 생성 실패: 폴더가 이미 존재합니다.");
        }

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
//        OutputStream file = new FileOutputStream("/Users/juncheol/mounttest/"+mid+"/"+filename); //
//        OutputStream file = new FileOutputStream("\\\\10.41.0.153\\storage\\"+mid+"\\"+filename);


        byte[] bt = monaco.getBytes(); //OutputStream은 바이트 단위로 저장됨
        file.write(bt);
        file.close();
        return filename;
    }

    //파일 삭제
    @PostMapping("/deleteFile")
    public String deleteJavaFile(@RequestBody Map<String, String> payload, Principal principal) throws Exception {
        String mid = principal.getName();
        String filename = payload.get("filename");
        System.out.println(filename);
        String rootDirectoryPath = "\\\\10.41.0.153\\storage\\"+mid+"\\java\\";
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

    //이름변경
    @PostMapping("/rename")
    public ResponseEntity<String> renameFile(
            @RequestParam("currentFilename") String currentFilename,
            @RequestParam("newFilename") String newFilename,
            @RequestParam("currentFolder") String currentFolder,
            Model model, Principal principal) {

        String mid = principal.getName();
        String rootDirectoryPath = "\\\\10.41.0.153\\storage\\"+mid+"\\java";

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
        } catch (IOException e) {
            e.printStackTrace();
            // 파일 이동 중 에러가 발생한 경우 에러 응답 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 이동 중 에러 발생");
        }

        return ResponseEntity.ok("파일이 성공적으로 이동되었습니다");
    }


    // 파일 저장 버튼
    @PostMapping("/saveFile")
    public ResponseEntity<Object> saveFile(@RequestBody Code code, Principal principal) {

        String mid = principal.getName();
//        String baseDir = "/Users/juncheol/mounttest/"+mid+"/java";
//        String filePath = baseDir + code.getFilename();
//        String filePath = baseDir + code.getFilename().replace("/", "\\");

        //파일 경로 확인
//        System.out.println("(MemoController:325) filePath : "+filePath);
        // TODO : 경로 수정
        String baseDir = "/Users/juncheol/mounttest/"+mid+"/java"; // 기본 경로
//        String baseDir = "\\\\10.41.0.153\\storage\\"+mid+"\\java";

//        String filePath = baseDir + code.getFilename().replace("/", "\\");
        String filePath = baseDir + code.getFilename();


        try {
            Path path = Paths.get(filePath);

            if (!Files.exists(path)) {
                return ResponseEntity.badRequest().body("파일이 존재하지 않습니다.");
            }

            // 파일 메타데이터 읽기
            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
//            String creationTime = attrs.creationTime().toString();
//            String lastModifiedTime = attrs.lastModifiedTime().toString();
            //

            LocalDateTime oriLastModifiedTime = attrs.lastModifiedTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime oriCreationTime = attrs.creationTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            String lastModifiedTime = oriLastModifiedTime.format(formatter);
            String creationTime = oriCreationTime.format(formatter);

            //파일의 inode 값 가져오기
            Object fileKey = attrs.fileKey();
            String inode = fileKey.toString().split("ino=")[1].split("\\)")[0];
            System.out.println("(MemoController:374) 파일 Inode 번호: " + inode);

            // debugfs 명령으로 파일의 세부정보 가져오기
            String command = "sudo debugfs -R 'stat <" + inode + ">' " + "dev/sdb1";
            String crtimePattern = "crtime:.*--\\s(.+)";
            Pattern pattern = Pattern.compile(crtimePattern);
            String formattedCrtime = "";
            //서버에 명령어 실행하고 결과값 가져오기
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", command);
                Process process = processBuilder.start();

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                SimpleDateFormat originalFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
                SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        String crtime = matcher.group(1);
                        try {
                            Date date = originalFormat.parse(crtime);
                            formattedCrtime = targetFormat.format(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }

                int exitCode = process.waitFor();
                System.out.println("Exited with code : " + exitCode);

                if (formattedCrtime != null) {
                    System.out.println("Formatted crtime: " + formattedCrtime);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }


            // 파일에 내용 쓰기
            Files.write(path, code.getContent().getBytes());

            // 생성일과 수정일 출력
//            System.out.println("(MemoController:) 파일 생성일: " + creationTime);
            System.out.println("(MemoController:) 파일 생성일: " + formattedCrtime);
            System.out.println("(MemoController:) 파일 수정일: " + lastModifiedTime);

            ResponseEntityDTO response = new ResponseEntityDTO();
            response.setMessage("파일 저장 완료");
            response.setCreationTime(formattedCrtime);
            response.setLastModifiedTime(lastModifiedTime);


            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("파일 저장 실패: " + e.getMessage());
        }
    }

    //파일 다운로드(우클릭)
    @PostMapping("/fileDownload")
    @ResponseBody
    public ResponseEntity<byte[]> downloadFile(@RequestBody Map<String, String> payload, Principal principal) throws IOException {

        String mid = principal.getName();
        String filename = payload.get("filename");
        String unZipFilePath = "\\\\10.41.0.153\\storage\\"+mid+"\\java"+filename;

        // 파일 경로로부터 파일을 읽어와 byte 배열로 변환
        File file = new File(unZipFilePath);
        byte[] fileContent = Files.readAllBytes(file.toPath());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", file.getName());

        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
    }


    //zip 다운로드
    @PostMapping("/zipDownload")
    @ResponseBody
    public ResponseEntity<Resource> zipDownload(@RequestBody Map<String, String> payload, Principal principal) throws Throwable {

        String mid = principal.getName();
        String filename = payload.get("filename");
        String [] filename2 = filename.split("[\\\\/]");
        String filename3 = filename2[filename2.length-1].replace(".java", "");

        // 압축을 해제할 위치, 압축할 파일이름, 파일위치+파일명
        String unZipPath = "\\\\10.41.0.153\\storage\\zip\\";
        String unZipFile = mid+"java"+filename3;
        String unZipFilePath = "\\\\10.41.0.153\\storage\\zip\\"+unZipFile+".zip";
        log.info("파일경로:"+unZipFilePath);


        log.info("============압축하기==============");
        CompressZip compressZip = new CompressZip();
        compressZip.compress("\\\\10.41.0.153\\storage\\"+mid+"\\java"+filename, unZipPath, unZipFile);

        // 압축 하기
        try {
            if (!compressZip.compress("\\\\10.41.0.153\\storage\\"+mid+"\\java"+filename, unZipPath, unZipFile)) {
                System.out.println("압축 실패");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }


        Resource fileSystemResource = new FileSystemResource(unZipFilePath);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileSystemResource.getFilename() + "\"")
                .contentType(MediaType.parseMediaType("application/zip"))
                .body(fileSystemResource);
    }

}
