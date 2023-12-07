package com.team36.controller;

import com.team36.domain.Editor;
import com.team36.domain.FileNode;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Controller
@Log4j2
@RequestMapping("/editor")
public class EditorController {

    @PostMapping("/get")
    @ResponseBody
    public String setFile(@RequestBody Editor editor) throws Exception{
        String filename = editor.getFilename();
        String content = editor.getContent();
        String filePath = "D:\\kimleeho\\savef\\" + filename;
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(content);
            fileWriter.flush();
        } catch (IOException e) {
            log.error("Error writing file: " + e.getMessage());
            throw new Exception("Error writing file", e);
        }

        log.info("filename : " + filename);
        log.info("content : " + content);
        return filename;
    }

    @PostMapping("/test2")
    @ResponseBody
    public String getFile(@RequestParam("filename2") String filename2) throws IOException {
        // 파일 경로
//        String filePath = "/Users/juncheol/Desktop/storage" + filename2;
//        String filePath = "D:\\hk\\project\\file\\" + filename2;
        String filePath = "D:\\kimleeho\\" + filename2;

        // 파일 내용을 읽어오는 메서드 호출
        String fileContent = readFile(filePath);
        System.out.println("fileContent : "+fileContent);
        System.out.println("filePath  : "+filePath);
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


    @GetMapping("/fileList")
    @ResponseBody
    public FileNode fileList() throws Exception {
//        String rootDirectoryPath = "/Users/juncheol/Desktop/storage";
        String rootDirectoryPath = "D:\\kimleeho";
        String targetDirectoryPath = rootDirectoryPath + "\\savef";
        FileNode root = new FileNode("savef", "\\savef"); // 상대 경로 사용
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


}


