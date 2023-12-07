package com.team36.controller;

import com.team36.domain.FileNode;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import java.util.Map;
import java.util.TreeMap;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home(Model model) throws Exception {

        return "index"; // Thymeleaf 템플릿 파일의 이름을 반환합니다.
    }

    @GetMapping("/editor")
    public String getEditor() throws Exception{
        return "editor";
    }

@GetMapping("/fileList")
@ResponseBody
public FileNode fileList() throws Exception {

        //유저 아이디와 프로젝트명 전달 받아서 처리 필요
        String userName = "";
        String projectName = "";
//    String rootDirectoryPath = "/Users/juncheol/Desktop/storage";
//    String targetDirectoryPath = rootDirectoryPath + "/user1";
//    FileNode root = new FileNode("user1", "/user1"); // 상대 경로 사용
    String rootDirectoryPath = "D:\\kimleeho";
    String targetDirectoryPath = rootDirectoryPath + "\\savef";
    FileNode root = new FileNode("savef", "\\savef");
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

    System.out.println(root);
    return root;
}

    private FileNode findOrCreateNode(FileNode root, String path, boolean isDirectory) {
//        FileNode current = root;
//        String[] parts = path.split(File.separator);
//        for (int i = 0; i < (isDirectory ? parts.length : parts.length - 1); i++) {
//            String part = parts[i];
//            if (part.isEmpty() || part.equals("user1")) continue; //
//
//            Optional<FileNode> found = current.getChildren().stream()
//                    .filter(node -> node.getName().equals(part))
//                    .findFirst();
//            if (found.isPresent()) {
//                current = found.get();
//            } else {
//                String nodePath = (current == root && i == 0) ? "/" + part : current.getText() + File.separator + part;
//                FileNode newNode = new FileNode(part, nodePath);
//                current.addChild(newNode);
//                current = newNode;
//            }
//        }
//        return current;
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
    @GetMapping("/download-zip")
    public ResponseEntity<Resource> downloadZip() throws IOException {
        String sourceDirPath = "/Users/juncheol/Desktop/storage/user1/dir1"; // 압축할 폴더 경로
        String zipFilePath = "/Users/juncheol/Desktop/storage/user1/zip/dir1.zip"; // 출력될 ZIP 파일 경로
//        String rootDirectoryPath = "/Users/juncheol/Desktop/storage";
//        String targetDirectoryPath = rootDirectoryPath + "/user1";
        compressToZip(sourceDirPath, zipFilePath);

        Resource fileSystemResource = new FileSystemResource(zipFilePath);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileSystemResource.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileSystemResource);
    }

    private void compressToZip(String sourceDir, String outputFile) throws IOException {
        Path zipPath = Files.createFile(Paths.get(outputFile));
        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            Path pp = Paths.get(sourceDir);
            Files.walk(pp)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
                        try {
                            zs.putNextEntry(zipEntry);
                            Files.copy(path, zs);
                            zs.closeEntry();
                        } catch (IOException e) {
                            System.err.println(e);
                        }
                    });
        }
    }


}
