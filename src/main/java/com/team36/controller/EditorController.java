package com.team36.controller;

import com.team36.domain.Editor;
import com.team36.domain.FileNode;
import com.team36.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Stream;

@Controller
@Log4j2
@RequiredArgsConstructor
public class EditorController {
    private final FileService fileService;

    @GetMapping("/editor")
    public String getEditor() throws Exception{

        return "editor";
    }

    @PostMapping("/editor/get")
    @ResponseBody
    public ResponseEntity<String> setFile(@RequestBody Editor editor, Model model) {
        String filename = editor.getFilename();
        String content = editor.getContent();
        String filePath = "D:\\kimleeho\\savef\\" + filename;

        if (new File(filePath).exists()) {
            String msg = "해당 파일명으로 저장하실 수 없습니다.(파일명 중복)";
            model.addAttribute("msg", msg);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }

        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(content);
            fileWriter.flush();
        } catch (IOException e) {
            // 파일 쓰기 오류 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 쓰기 오류");
        }

        return ResponseEntity.ok("파일이 성공적으로 저장되었습니다");
    }

    @PostMapping("/editor/autoSave")
    @ResponseBody
    public ResponseEntity<String> autoSave(@RequestBody Editor editor){
        String filename = editor.getFilename();
        String content = editor.getContent();
        String filePath = "D:\\kimleeho\\autosave\\" + filename;

        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(content);
            fileWriter.flush();
        } catch (IOException e) {
            // 파일 쓰기 오류 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 쓰기 오류");
        }

        return ResponseEntity.ok("파일이 자동 저장되었습니다.");
    }

    @PostMapping("/editor/test2")
    @ResponseBody
    public String getFile(@RequestParam("filename2") String filename2) throws IOException {
        // 파일 경로
//        String filePath = "/Users/juncheol/Desktop/storage" + filename2;
//        String filePath = "D:\\hk\\project\\file\\" + filename2;
//        String filePath = "C:\\kimleeho\\" + filename2;
        String filePath = "D:\\kimleeho\\" + filename2;

        // 파일 내용을 읽어오는 메서드 호출
        String fileContent = readFile(filePath);
        System.out.println("fileContent : " + fileContent);
        System.out.println("filePath  : " + filePath);
        log.info("filename : " + filename2);
        log.info("filePath : " + filePath);
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
        } catch (FileNotFoundException e) {
            // 파일을 찾을 수 없는 경우 처리
            log.error("File not found: " + filePath);
            return "File not found";
        }
    }




    @GetMapping("/editor/fileList")
    @ResponseBody
    public FileNode fileList() throws Exception {
//        String rootDirectoryPath = "/Users/juncheol/Desktop/storage";
        String rootDirectoryPath = "D:\\kimleeho";
//        String rootDirectoryPath = "C:\\kimleeho";
        String targetDirectoryPath = rootDirectoryPath + "\\savef";
        FileNode root = new FileNode("savef", "savef"); // 상대 경로 사용
        List<Path> directories = new ArrayList<>();
        List<Path> files = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(Paths.get(targetDirectoryPath))) {
            paths.forEach(path -> {
                if (!path.equals(Paths.get(targetDirectoryPath))) { // 루트 디렉토리 제외
                    if (Files.isDirectory(path)) {
                        directories.add(path);
                    } else {
                        files.add(path);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            // 예외 처리
        }

        Comparator<Path> pathComparator = Comparator.comparing(Path::toString);
        directories.sort(pathComparator);
        files.sort(pathComparator);

        // 디렉토리 노드 추가
        directories.forEach(dir -> {
            String dirRelativePath = dir.toString().substring(rootDirectoryPath.length());
            findOrCreateNode(root, dirRelativePath, true);
        });

        // 파일 노드 추가
        files.forEach(file -> {
            String fileRelativePath = file.toString().substring(rootDirectoryPath.length());
            String parentDirPath = fileRelativePath.substring(0, fileRelativePath.lastIndexOf(File.separator));
            FileNode parentNode = findOrCreateNode(root, parentDirPath, true); // 파일의 상위 디렉토리 노드 찾기
            parentNode.addChild(new FileNode(file.getFileName().toString(), fileRelativePath)); // 파일 노드 추가
        });

        return root;
    }

    private FileNode findOrCreateNode(FileNode root, String path, boolean isDirectory) {
        FileNode current = root;
        String[] parts = path.split("\\\\");
        for (int i = 0; i < (isDirectory ? parts.length : parts.length - 1); i++) {
            String part = parts[i];
            if (part.isEmpty() || part.equals("savef")) continue;

            Optional<FileNode> found = current.getChildren().stream()
                    .filter(node -> node.getName().equals(part))
                    .findFirst();
            if (found.isPresent()) {
                current = found.get();
            } else {
                String nodePath = (current == root && i == 0) ? "\\" + part : current.getText() + "\\" + part;
                FileNode newNode = new FileNode(part, nodePath);
                current.addChild(newNode);
                current = newNode;
            }
        }
        return current;
    }

    @PostMapping("/delete")
    public String deleteFile(@RequestBody Map<String, String> payload) throws Exception {
        String filename = payload.get("filename");
        // 파일 또는 폴더를 삭제할 디렉토리 경로
        String rootDirectoryPath = "D:\\kimleeho";
//        String rootDirectoryPath = "C:\\kimleeho";
        String filePath = rootDirectoryPath + filename;

        // File 객체 생성
        File fileToDelete = new File(filePath);

        // 파일 또는 폴더가 존재하는지 확인
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
            // 파일 또는 폴더가 존재하지 않을 때의 처리를 추가할 수 있습니다.
        }
        return "redirect:/fileList";
    }

    @PostMapping("/editor/rename")
    public ResponseEntity<String> renameFile(
            @RequestParam("currentFilename") String currentFilename,
            @RequestParam("newFilename") String newFilename,
            @RequestParam("currentFolder") String currentFolder,
            Model model) {
        System.out.println("현재 파일 이름: " + currentFilename);
        System.out.println("바꿀 파일 이름: " + newFilename);
        System.out.println("현재 디렉토리: " + currentFolder);

        String rootDirectoryPath = "D:\\kimleeho";

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

    @PostMapping("/editor/readFile")
    public ResponseEntity<String> readFile(@RequestParam("file") MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            //파일 확장자 별로 content 변수명이 달라져야됨.
            System.out.println("File name: " + file.getOriginalFilename());
            System.out.println("Content type: " + file.getContentType());
            System.out.println("File size: " + file.getSize() + " bytes");
            try (InputStream inputStream = file.getInputStream();
                 InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                 BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

                StringBuilder content = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    content.append(line).append("\n");
                }

                // 파일 내용 출력 또는 원하는 파일 처리 로직을 추가합니다.
                System.out.println("File content:\n" + content);

                return ResponseEntity.ok("파일이 성공적으로 이동되었습니다");
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("파일 처리 중 오류가 발생했습니다: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일이 비어 있거나 존재하지 않습니다.");
        }
    }
}


