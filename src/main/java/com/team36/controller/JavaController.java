package com.team36.controller;

import com.team36.domain.DragFile;
import com.team36.domain.FileNode;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
//      String rootDirectoryPath = "/Users/juncheol/mounttest/";
//        String rootDirectoryPath = "/Users/juncheol/Desktop/storage/";

        // 이호
//        String rootDirectoryPath = "D:\\kimleeho";

        //현경
//        String rootDirectoryPath = "\\\\10.41.0.153\\storage\\";
        String rootDirectoryPath = "\\\\10.41.0.153\\team36\\";

//        String rootDirectoryPath = "\\\\10.41.0.153\\team36\\";
//        String rootDirectoryPath = "C:\\hkdev\\proj\\storage\\";

        String targetDirectoryPath = rootDirectoryPath+mid + "\\java";
        FileNode root = new FileNode("java", "",mid+"\\java" );
        //경로 확인
//        System.out.println("(JavaController:59) targetDirectoryPath : "+targetDirectoryPath);

        // File 객체 생성
        File targetDirectory = new File(targetDirectoryPath);
        // 디렉토리가 존재하지 않으면 생성
        if (!targetDirectory.exists()) {
            targetDirectory.mkdirs();

            String codeFileName = "New.java";
            String code = "public class " + codeFileName.replace(".java", "") + " {\n" +
                    "\tpublic static void main(String[] args) {\n" +
                    "\t\t\n" +
                    "\t}\n" +
                    "}";
            Path codeFilePath = Paths.get(targetDirectoryPath, codeFileName);
            try {
                Files.write(codeFilePath, code.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                // 예외 처리
            }

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
            parentNode.addChild(new FileNode(file.getFileName().toString(), fileRelativePath,mid+"\\java")); // 파일 노드 추가
        });
        System.out.println(root.getChildren());
        return root.getChildren();
    }


    private FileNode findOrCreateNode(FileNode root, String path, boolean isDirectory, Principal principal) {

        String mid = principal.getName();

        // TODO : 경로 수정   윈도우 -> \\
        FileNode current = root;
//        String[] parts = path.split("/");
        String[] parts = path.split("\\\\");
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

//                String nodePath = (current == root && i == 0) ? "/" + part : current.getText() + "/" + part;
                String nodePath = (current == root && i == 0) ? "\\" + part : current.getText() + "\\" + part;
                FileNode newNode = new FileNode(part, nodePath,mid+"\\java");
                current.addChild(newNode);
                current = newNode;
            }
        }

        return current;
    }

    @PostMapping("/drag")
    public ResponseEntity<String> moveFile(@RequestBody DragFile fileMoveRequest, Principal principal) {
        String mid = principal.getName();

        try {
            // TODO : 경로 수정
//            String baseDir = "/Users/juncheol/mounttest/" + mid + "/" + "java";
            String baseDir = "\\\\10.41.0.153\\team36\\" + mid + "\\" + "java";
//            String baseDir = "C:\\kimleeho\\savef\\" +mid + "\\" +html;
            String filePath = baseDir + fileMoveRequest.getFilehref();
            String folderPath = baseDir + fileMoveRequest.getFolderhref();
            System.out.println("파일로 위치잡았을때 값: " + fileMoveRequest.getFolderhref());
            File file = new File(filePath);
            File folder = new File(folderPath);

            if (file.exists() && folder.exists()) {
                File newFile = new File(folder, file.getName());
                if (file.renameTo(newFile)) {
                    return ResponseEntity.ok("파일 이동 성공");
                } else {
                    return ResponseEntity.status(500).body("파일 이동 실패");
                }
            } else {
                return ResponseEntity.status(400).body("파일 또는 폴더가 존재하지 않습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("서버 오류");
        }
    }
//    @GetMapping("/download-zip")
//    public ResponseEntity<Resource> downloadZip(Principal principal) throws IOException {
//
//        String mid = principal.getName();
////        String sourceDirPath = "/Users/juncheol/Desktop/team36/user1/dir1"; // 압축할 폴더 경로
////        String zipFilePath = "/Users/juncheol/Desktop/team36/user1/zip/dir1.zip"; // 출력될 ZIP 파일 경로
//        String sourceDirPath ="\\\\10.41.0.153\\team36\\"+mid+"\\java"; // 압축할 폴더 경로
//        String zipFilePath = "\\\\10.41.0.153\\team36\\"+mid+"\\java\\java.zip"; // 출력될 ZIP 파일 경로
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

    /////폴더이름변경
    @PostMapping("/renamefolder")
    public ResponseEntity<String> renameFolder(
            @RequestParam("currentFoldername") String currentFoldername,
            @RequestParam("newFoldername") String newFoldername,
            @RequestParam("currentFolder") String currentFolder,
            Model model, Principal principal) {

        String mid = principal.getName();
        String java = "java";
        String rootDirectoryPath = "\\\\10.41.0.153\\team36"+"\\"+mid+"\\"+java;
//        String rootDirectoryPath = "C:\\kimleeho\\savef\\" + mid + "\\" + html;

        // 현재 폴더의 경로와 새로운 폴더의 경로를 구성
        String currentFolderPath = rootDirectoryPath + currentFolder;
        String newFolderPath = rootDirectoryPath +currentFoldername+"\\"+ newFoldername;

        // 현재 폴더와 새로운 폴더의 Path 객체 생성
        Path folder = Paths.get(currentFolderPath);
        Path newFolder = Paths.get(newFolderPath);

        // 중복 폴더명 체크
        if (Files.exists(newFolder)) {
            String msg = "해당 폴더명으로 저장하실 수 없습니다. (폴더명 중복)";
            model.addAttribute("msg", msg);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }

        try {
            // 폴더 이동
            Files.move(folder, newFolder, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            // 폴더 이동 중 에러가 발생한 경우 에러 응답 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("폴더 이동 중 에러 발생");
        }

        return ResponseEntity.ok("폴더가 성공적으로 이동되었습니다");
    }
}
