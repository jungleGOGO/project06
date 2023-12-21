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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
@RequestMapping("/java")
public class JavaController {
    @GetMapping("/project")
    public String javaHome(Principal principal, Model model) throws Exception{

        String mid = principal.getName();
        model.addAttribute("mid",mid);
//        System.out.println("project mid : "+ mid);
        return "java";
    }

    @GetMapping("/fileList")
    @ResponseBody
    public List<FileNode> fileList(Principal principal) throws Exception {

        String mid = principal.getName();
        // TODO : 경로 수정
        // 준철
      String rootDirectoryPath = "/Users/juncheol/mounttest/";
//        String rootDirectoryPath = "/Users/juncheol/Desktop/storage";

        // 이호
//        String rootDirectoryPath = "D:\\kimleeho";

        //현경
//        String rootDirectoryPath = "\\\\10.41.0.153\\storage\\";
        String targetDirectoryPath = rootDirectoryPath+mid + "/java";
        FileNode root = new FileNode("java", "",mid+"/java" );
        //경로 확인
//        System.out.println("(JavaController:59) targetDirectoryPath : "+targetDirectoryPath);

        // File 객체 생성
        File targetDirectory = new File(targetDirectoryPath);
        // 디렉토리가 존재하지 않으면 생성
        if (!targetDirectory.exists()) {
            targetDirectory.mkdirs();
        }

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
            String dirRelativePath = dir.toString().substring(targetDirectoryPath.length());
            findOrCreateNode(root, dirRelativePath, true,principal);
        });


        // 파일 노드 추가
        files.forEach(file -> {
            String fileRelativePath = file.toString().substring(targetDirectoryPath.length());
            String parentDirPath = fileRelativePath.substring(0, fileRelativePath.lastIndexOf(File.separator));
            FileNode parentNode = findOrCreateNode(root, parentDirPath, true,principal); // 파일의 상위 디렉토리 노드 찾기
            parentNode.addChild(new FileNode(file.getFileName().toString(), fileRelativePath,mid+"/java")); // 파일 노드 추가
        });
        System.out.println(root.getChildren());
        return root.getChildren();
    }


    private FileNode findOrCreateNode(FileNode root, String path, boolean isDirectory, Principal principal) {

        String mid = principal.getName();

        // TODO : 경로 수정   윈도우 -> \\
        FileNode current = root;
        String[] parts = path.split("/");
//        String[] parts = path.split("/");
        for (int i = 0; i < (isDirectory ? parts.length : parts.length - 1); i++) {
            String part = parts[i];

            if (part.isEmpty() || part.equals(mid)) continue;


            Optional<FileNode> found = current.getChildren().stream()
                    .filter(node -> node.getName().equals(part))
                    .findFirst();
            if (found.isPresent()) {
                current = found.get();

            } else {

                String nodePath = (current == root && i == 0) ? "/" + part : current.getText() + "/" + part;
//                String nodePath = (current == root && i == 0) ? "\\" + part : current.getText() + "\\" + part;
                FileNode newNode = new FileNode(part, nodePath,mid+"/java");
                current.addChild(newNode);
                current = newNode;
            }
        }

        return current;
    }

//    @GetMapping("/download-zip")
//    public ResponseEntity<Resource> downloadZip(Principal principal) throws IOException {
//
//        String mid = principal.getName();
////        String sourceDirPath = "/Users/juncheol/Desktop/storage/user1/dir1"; // 압축할 폴더 경로
////        String zipFilePath = "/Users/juncheol/Desktop/storage/user1/zip/dir1.zip"; // 출력될 ZIP 파일 경로
//        String sourceDirPath ="\\\\10.41.0.153\\storage\\"+mid+"\\java"; // 압축할 폴더 경로
//        String zipFilePath = "\\\\10.41.0.153\\storage\\"+mid+"\\java\\java.zip"; // 출력될 ZIP 파일 경로
//
//        compressToZip(sourceDirPath, zipFilePath);
//
//        Resource fileSystemResource = new FileSystemResource(zipFilePath);
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileSystemResource.getFilename() + "\"")
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .body(fileSystemResource);
//    }
//
//    private void compressToZip(String sourceDir, String outputFile) throws IOException {
//        Path zipPath = Files.createFile(Paths.get(outputFile));
//        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(zipPath))) {
//            Path pp = Paths.get(sourceDir);
//            Files.walk(pp)
//                    .filter(path -> !Files.isDirectory(path))
//                    .forEach(path -> {
//                        ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
//                        try {
//                            zs.putNextEntry(zipEntry);
//                            Files.copy(path, zs);
//                            zs.closeEntry();
//                        } catch (IOException e) {
//                            System.err.println(e);
//                        }
//                    });
//        }
//    }
}
