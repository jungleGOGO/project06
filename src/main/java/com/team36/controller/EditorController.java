package com.team36.controller;

import com.team36.domain.*;
import com.team36.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import java.security.Principal;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
@Log4j2
@Slf4j
@RequiredArgsConstructor
public class EditorController {
    private final FileService fileService;

    @GetMapping("/editor")
    public String getEditor(Principal principal, Model model) throws Exception{
        boolean loginCheck = false;
        if(principal == null) {
            loginCheck = false;

        } else {
            loginCheck = true;

        }
        model.addAttribute("loginCheck", loginCheck);
        return "editor";
    }
//@ResponseBody는 Spring mvc 컨트롤러 매서드가 http 응답의 본문(body)으로 직접 데이터 반환시 사용.
    //ResponseEntity<T>는 http 응답의 상태 코드,헤어,본문등을 포함하는 역할. T는 응답 본문의 데이터 유형.
    //ResponseEntity는 상태코드, 헤더, 본문 등을 세밀하게 제어가능
    //ResponseBody는 메서드가 직접 응답의 본문만을 반환한다
    //클라이언트에서 객체 형식으로 데이터를 보냈을때는 @RequestParam보다는 @RequestBody를 사용한다.
@PostMapping("/editor/save")
@ResponseBody
public ResponseEntity<String> handleFileUpload(
        @RequestBody Code code, Model model, Principal principal) {
    String mid = principal.getName();
    String html = "html";

    try {
        String filename = code.getFilename();
        String content = code.getContent();
        System.out.println("저장기능 파일이름: "+filename);

        String filePath =  "//10.41.0.153/storage/" + mid + "/" + html+"/";
        File targetDirectorys = new File(filePath);
        System.out.println(filePath);
        // 디렉토리가 존재하지 않으면 생성
        if (!targetDirectorys.exists()) {
            targetDirectorys.mkdirs();
        }


//         중복 파일명 체크 함수

//        if (isFileExists(filePath,filename)) {
//            String msg = "해당 파일명으로 저장하실 수 없습니다.(파일명 중복)";
//            model.addAttribute("msg", msg);
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
//        }

        // 파일 생성 및 쓰기
        writeFile(filePath+filename, content);

        return ResponseEntity.ok("파일이 성공적으로 저장되었습니다");
    } catch (IOException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 쓰기 오류");
    }
}

    private boolean isFileExists(String folderPath, String fileName) {
        File file = new File(folderPath, fileName);
        return file.exists() && file.isFile();
    }

    private void writeFile(String filePath, String content) throws IOException {
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(content);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace(); // 예외가 발생하면 콘솔에 출력
            throw e; // 예외를 다시 던져서 상위 메서드로 전파
        }
    }

    @PostMapping("/editor/renamesave")
    @ResponseBody
    public ResponseEntity<String> renamesave(
            @RequestBody Code code, Model model, Principal principal) {
        String mid = principal.getName();
        String html = "html";

        try {
            String filename = code.getFilename();
            String content = code.getContent();

            String filePath =  "//10.41.0.153/storage/" + mid + "/" + html+"/";
            File targetDirectorys = new File(filePath);

            // 디렉토리가 존재하지 않으면 생성
            if (!targetDirectorys.exists()) {
                targetDirectorys.mkdirs();
            }


//         중복 파일명 체크 함수

        if (isFileExists(filePath,filename)) {
            String msg = "해당 파일명으로 저장하실 수 없습니다.(파일명 중복)";
            model.addAttribute("msg", msg);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }

            // 파일 생성 및 쓰기
            writeFile(filePath+filename, content);

            return ResponseEntity.ok("파일이 성공적으로 저장되었습니다");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 쓰기 오류");
        }
    }

    @PostMapping("/editor/newsave")
    @ResponseBody
    public ResponseEntity<String> newsave(
            @RequestBody Code code, Model model, Principal principal) {
        String mid = principal.getName();
        String html = "html";

        try {
            String filename = code.getFilename();
            String content = code.getContent();

            String filePath =  "//10.41.0.153/storage/" + mid + "/" + html+"/";
            File targetDirectorys = new File(filePath);

            // 디렉토리가 존재하지 않으면 생성
            if (!targetDirectorys.exists()) {
                targetDirectorys.mkdirs();
            }


//         중복 파일명 체크 함수

            if (isFileExists(filePath,filename)) {
                String msg = "해당 파일명으로 저장하실 수 없습니다.(파일명 중복)";
                model.addAttribute("msg", msg);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
            }

            // 파일 생성 및 쓰기
            writeFile(filePath+filename, content);

            return ResponseEntity.ok("파일이 성공적으로 저장되었습니다");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 쓰기 오류");
        }
    }





    @PostMapping("/editor/autoSave")
    @ResponseBody
    public ResponseEntity<String> autoSave(@RequestBody Code code,Principal principal){
        String mid = principal.getName();
        String filename = code.getFilename();
        String content = code.getContent();
        System.out.println("자동저장 파일이름:"+filename);
        String html = "html";
        String filePath = "//10.41.0.153/storage/" + mid + "/" + html+"/" + filename;

        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(content);
            fileWriter.flush();
        } catch (IOException e) {
            // 파일 쓰기 오류 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 쓰기 오류");
        }

        return ResponseEntity.ok("파일이 자동 저장되었습니다.");
    }

    @PostMapping("/editor/read")
    @ResponseBody
    public String getFile(@RequestParam("filename2") String filename2,Principal principal) throws IOException {
      String mid = principal.getName();
      String html = "html";
        // 파일 경로
//        String filePath = "/Users/juncheol/Desktop/storage" + filename2;
//        String filePath = "D:\\hk\\project\\file\\" + filename2;
//        String filePath = "C:\\kimleeho\\" + filename2;
//        String filePath = "D:\\kimleeho\\" + filename2;
        String filePath = "//10.41.0.153/storage/" + mid +"/"+html+"/" + filename2;

        // 파일 내용을 읽어오는 메서드 호출
        String fileContent = readFile(filePath);
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
    public List<FileNode> fileList(Principal principal) throws Exception {
        String mid = principal.getName();
        String html = "html";
        String rootDirectoryPath = "\\\\10.41.0.153\\storage\\"+mid;

        String targetDirectoryPath = rootDirectoryPath + "\\"+html;
        FileNode root = new FileNode(html, "\\"+html, mid); // 상대 경로 사용

//        String rootDirectoryPath = "D:\\kimleeho"; //파일 및 디렉토리를 읽어올 루트 디렉토리 경로
//        String rootDirectoryPath = "C:\\kimleeho";
//        String targetDirectoryPath = rootDirectoryPath + "\\savef"; // 실제로 읽어올 대상 디렉토리 경로 설정
//        FileNode root = new FileNode("savef", "savef"); // 루트 노드를 생성하고 초기화. 루트 노드는 트리의 시작점이다. savef라는 이름을 가진 파일또는 디렉토리이며, savef라는 파일또는 디렉토리경로(상대경로)

        /*  상대경로는 현재 작업 디렉토리 또는 기준 경로같은 특정 위치에서 시작하여 목표 위치까지의 경로를 상대적으로 설명한다.
  상대경로는 파일이나 디렉토리 간의 상대적인 위치를 나타내기 때문에, 현재 작업 디렉토리가 변경되더라도 해당 위치를 나타내는데 영향 받지 않는다.*/

        // File 객체 생성
        File targetDirectory = new File(targetDirectoryPath);

        // 디렉토리가 존재하지 않으면 생성
        if (!targetDirectory.exists()) {
            targetDirectory.mkdirs();
        }
        //Path는 NIO패키지 인터페이스이며 파일 또는 디렉토리의 경로를 나타내는데 사용.
        List<Path> directories = new ArrayList<>(); // 디렉토리를 저장할 리스트
        List<Path> files = new ArrayList<>(); // 파일을 저장할리스트

/*Files.walk는 해당 디렉토리및 하위 디렉토리에 대한 모든 파일과 디렉토리 경로를 재귀적으로 반환,
 - Stream은 데이터를 처리하는데 사용되며, 데이터 원본에 상관없이 동일한 방식으로 작업할 수 있도록 함. 스트림은 데이터를 필터링,변환,그룹화,정렬하는 등 다양한 작업을 수행할 수 있다.
 - 재귀적으로 반환한다는 것- 어떤 함수나 메서드가 자기 자신을 호출하여 작업을 수행하고 그 결과를 다시 호출한 곳으로 반환하는 과정을 의미한다.
 ㄴ> 이는 함수나 메서드가 자신을 여러번 호출하여 더 작은 문제들을 해결하고, 이를 조합하여 전체 문제르 해결하는 방식이다.

   */
        try (Stream<Path> paths = Files.walk(Paths.get(targetDirectoryPath))) {
            paths.forEach(path -> { //스트림의 각 요소인 파일및 디렉토리의 Path 객체에 대해 주어진 작업 수행
                if (!path.equals(Paths.get(targetDirectoryPath))) { // 현재 순회중인 'path'가 루트 디렉토리인 경우(지정된 대상 디렉토리 자체),루트 디렉토리를 제외하고 리스트에 추가하는 작업 수행
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
/*
Comparator- 객체들을 비교하기 위한 인터페이스. 객체들의 순서를 결정하는데 활용.
Comparator.comparing은 객체의 특정 키(key)를 추출하여 비교할 때 사용된다. 객체를 특정 키로 매핑하는 역할을함.
Path::toString은 Path 객체를 문자열로 변환함. Path 객체를 문자열로 매핑하는 역할.
 ::은 메서드 레퍼런스 나타내는 연산자. 메서드 레퍼런스는 람다 표현식을 단순화하기 위한 기능이며 이미 존재하는 메서드나 생성자를 가리키는데 사용된다.
 Path::toString에서 ::는 Path 객체의 toString 메서드를 참조하라는 의미다.
 */
        //현재 오름차순으로 정렬중 - 사전순서에 따른 오름차순. a가 먼저나옴
        Comparator<Path> pathComparator = Comparator.comparing(Path::toString);
        directories.sort(pathComparator);
        files.sort(pathComparator);

        // 디렉토리 노드 추가
        directories.forEach(dir -> {
            //디렉토리의 상대경로를 계산. rootDirectory는 루트 디렉토리의 경로이며 이를 기준으로 디렉토리의 상대경로를 계산한다.
            String dirRelativePath = dir.toString().substring(targetDirectoryPath.length());
//           트리에 디렉토리 노드를추가하거나 이미 존재하는 노드를 찾는다. true는 디렉토리라는 것을 나타낸다
            findOrCreateNode(root, dirRelativePath, true,principal);
        });

        // 파일 노드 추가
        files.forEach(file -> {
            String fileRelativePath = file.toString().substring(targetDirectoryPath.length());//파일의 상대 경로를 계산
            String parentDirPath = fileRelativePath.substring(0, fileRelativePath.lastIndexOf(File.separator));//파일의 상위 디렉토리 경로를 계산
            FileNode parentNode = findOrCreateNode(root, parentDirPath, true,principal); // 파일의 상위 디렉토리 노드 찾기
            parentNode.addChild(new FileNode(file.getFileName().toString(), fileRelativePath,mid)); // 상위 디렉토리에 파일 노드 추가

        });

        System.out.println("respin"+root.getChildren());
        return root.getChildren();



    }


//주어진 FileNode 트리에서 특정 경로에 해당하는 노드를 찾거나 새로운 노드를 생성하여 반환하는 역할
    private FileNode findOrCreateNode(FileNode root, String path, boolean isDirectory, Principal principal) {
        String mid = principal.getName();
        String html="html";
        FileNode current = root; //현재 노드를 루트 노드로 초기화
        String[] parts = path.split("\\\\"); // 주어진 경로를 \로 분할하여 배열로 저장.

        // 반복문을 통해 각 경로의 구성요소에 대해 처리. 만약 디렉토리인 경우 모든 구성요소 처리. 파일인 경우에는 마지막 구성요소를 제외하고 처리
       /*
       삼항연산자- (조건식 ? T : F) - 조건식이 참이면 T가 실행, 거짓이면 F가 실행
        */
        for (int i = 0; i < (isDirectory ? parts.length : parts.length - 1); i++) {
            String part = parts[i]; // 현재 반복에서 처리할 경로의 일부를 가져온다
            if (part.isEmpty() || part.equals(html)) continue;

            // current.getChildren()는 current에 해당하는 FileNode객체의 자식 노드 목록을 가져옴
            Optional<FileNode> found = current.getChildren().stream()
                    .filter(node -> node.getName().equals(part)) // 노드의 이름이 part와 같은지 확인하고 필터링한다.
                    .findFirst(); //필터링된 노드 중 첫번째 것을 선택
            if (found.isPresent()) { // 일치하는 노드가 존재하면 현재 노드를 해당노드로 업데이트
                current = found.get();
            } else {// 일치하는 노드가 없으면 새로운 노드를 생성
                //초기 루트 노드이고 첫 번째 구성요소인 경우에는 \를 추가하고, 그렇지 않은 경우에는 현재 노드의 텍스트와 구성요소를 결합.
                String nodePath = (current == root && i == 0) ? "\\" + part : current.getText() + "\\" + part;
//                새로운 노드를 생성
                FileNode newNode = new FileNode(part, nodePath,mid);

                current.addChild(newNode);//현재 노드에 새로운 노드를 자식으로 추가
                current = newNode; //현재 노드를 새로운 노드로 업데이트
            }
        }

        return current;
    }



    @PostMapping("/editor/delete")
    public String deleteFile(@RequestBody Map<String, String> payload, Principal principal) throws Exception {
        String filename = payload.get("filename");
        String html = "html";
        // 파일 또는 폴더를 삭제할 디렉토리 경로
//        String rootDirectoryPath = "D:\\kimleeho";
        String mid = principal.getName();
        String rootDirectoryPath = "\\\\10.41.0.153\\storage"+"\\"+mid+"\\"+html;
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
        } else {
            System.out.println("삭제할 파일 또는 폴더가 존재하지 않습니다: " + filePath);
            // 파일 또는 폴더가 존재하지 않을 때의 처리를 추가할 수 있습니다.
        }
        return "redirect:/editor/fileList";
    }

    @PostMapping("/editor/rename")
    public ResponseEntity<String> renameFile(
            @RequestParam("currentFilename") String currentFilename,
            @RequestParam("newFilename") String newFilename,
            @RequestParam("currentFolder") String currentFolder,
            Model model,Principal principal) {

        String mid = principal.getName();
        String html = "html";

        String rootDirectoryPath = "\\\\10.41.0.153\\storage"+"\\"+mid+"\\"+html;


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



    @PostMapping("/editor/readFile")
    @ResponseBody
    public String readFile(@RequestParam("file") MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            // 파일이 비어 있지 않은 경우에만 처리
            String fileName = file.getOriginalFilename();
            String fileExtension = getFileExtension(fileName);

            try (InputStream inputStream = file.getInputStream();
                 InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                 BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

                StringBuilder content = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    content.append(line).append("\n");
                }

                // 파일 확장자에 따라 처리된 내용을 반환
                return processFileContentByExtension(content.toString(), fileExtension);

            } catch (IOException e) {
                e.printStackTrace();
                // 오류가 발생한 경우 빈 문자열 반환 또는 다른 오류 처리 방식 선택 가능
                return "";
            }
        } else {
            // 파일이 비어 있거나 존재하지 않는 경우 빈 문자열 반환 또는 다른 처리 방식 선택 가능
            return "";
        }
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex + 1);
        }
        return "";
    }

    private String processFileContentByExtension(String content, String fileExtension) {
        switch (fileExtension.toLowerCase()) {
            case "html":
            case "txt":
                return content; // HTML 및 TXT 파일의 경우 그대로 반환
            case "css":
            case "scss":
                // CSS 및 SCSS 파일의 경우 CSS 텍스트 에어리어에 값 할당
                return "<style>\n" + content + "\n</style>";
            case "js":
                // JS 파일의 경우 JS 텍스트 에어리어에 값 할당
                return "<script>\n" + content + "\n</script>";
            default:
                // 다른 확장자의 경우 빈 문자열 반환 또는 다른 처리 방식 선택 가능
                return "";
        }
    }

    @PostMapping("/editor/mkdir")
    public ResponseEntity<?>  createDirectory(@RequestBody Directory directory,Principal principal) {
        System.out.println("받아온 디레고리 경로 값:"+directory.getPath());
        String webPath = directory.getPath(); // 웹 경로 (/user1/dir1 형식)
        String mkdirname = directory.getMkdirname(); // 생성할 디렉토리 이름
        String mid = principal.getName();
        String html = "html";
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
        String baseDir = "\\\\10.41.0.153\\storage\\" +mid + "\\" +html;
        String filePath = baseDir + webPath.replace("\\", File.separator);
        System.out.println("폴더생성 filepath:"+filePath);

        Path directoryPath;

        File file = new File(filePath);
        System.out.println("if 블록 진입 전");
        if (file.isDirectory()) {
            // 디렉토리인 경우
            directoryPath = Paths.get(filePath, mkdirname);
            System.out.println("ㅋㅋㅋㅋㅋ"+directoryPath);
        }  else {
            // 파일인 경우
            directoryPath = file.toPath().getParent().resolve(mkdirname);
            System.out.println("파일인경우"+directoryPath);
        }
        System.out.println("directoryPath : "+directoryPath);
        try {
            System.out.println("폴더 생성 시도 경로: " + directoryPath.toString());
            Files.createDirectories(directoryPath);
            System.out.println("디렉토리 생성 후 경로: " + directoryPath.toString());
            return ResponseEntity.ok("폴더 생성 완료: " + directoryPath.toString());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("폴더 생성 실패: " + e.getMessage());
        }
    }
}




