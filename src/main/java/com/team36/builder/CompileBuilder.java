package com.team36.builder;

import com.team36.util.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CompileBuilder {
    // private final String path = CompilerApplication.class.getProtectionDomain().getCodeSource().getLocation().getPath();

    // TODO : 경로 수정
//    private final String path = "/Users/juncheol/Desktop/compile/";
//    private final String path = "D:\\hk\\project\\compile\\";
    private final String path = "D:\\compile\\";

    // 파일명이랑 코드내용 전달 받음
    // 파일명 .java 아니면 alert띄우도록 프론트에서 처리
    // 메인메소드가 있어야 실행 가능

    //- 타임아웃
    //- 명령 제한
    //- 에러메시지 경로 표시


//    private static final Logger logger = LoggerFactory.getLogger(CompileBuilder.class);
    private static final Logger compileAndRunLogger = LoggerFactory.getLogger(CompileBuilder.class);

    //전달받은 코드 컴파일하고 실행 결과 반환
    public String compileAndRunCode(String code, String fileName, String mid) {
//        System.out.println(code);
//        System.out.println("fileName"+fileName);
        int dotIndex = fileName.lastIndexOf(".");
        String noExtensionFileName = fileName.substring(0, dotIndex); // 파일 확장자 잘라내기

        // 로깅을 위한 시간 가져오기
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String formattedNow = now.format(formatter);



        // 악의적인 코드 유무 확인
        if (!chkCode(code)) {
            return "실행할 수 없는 코드입니다.";
        }

        String uuid = UUID.randomUUID().toString();
        String uuidPath = path + uuid + "/";
        File newFolder = new File(uuidPath);

        compileAndRunLogger.info(formattedNow + " " + mid  + " - 컴파일 및 실행 시작 " + uuidPath); // 로깅
///////////////////////////////////////////// 자바 파일 생성 /////////////////////////////////////////////////////
        // 디렉토리 생성 시도
        if (!newFolder.mkdir()) {
            // 디렉토리 생성 실패 시 로그 기록 및 에러 메시지 반환
            compileAndRunLogger.error(formattedNow + " " + mid  +" 디렉토리 생성 오류: " + uuidPath);
            return "디렉토리를 생성할 수 없습니다.";
        }
        // 새 Java 소스 파일 생성
        File sourceFile = new File(uuidPath + fileName);
        // 제공된 코드를 파일에 쓰기
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(sourceFile))) {
            writer.write(code);
        } catch (IOException e) {
            // 파일 쓰기 실패 시 로그 기록 및 에러 메시지 반환
            compileAndRunLogger.error(formattedNow + " " + mid  + " 소스 코드 파일 작성 오류: " + e.getMessage());
            return "소스 코드 파일을 작성할 수 없습니다.";
        }

/////////////////////////////////////////////////  컴파일  ///////////////////////////////////////////////////////
        // 비동기 작업 실행을 위한 ExecutorService 생성
        ExecutorService executor = Executors.newSingleThreadExecutor();

        StringBuilder output = new StringBuilder();
        // Stirng은 변경 불가능한 문자열을 생성하지만, StringBuilder는 변경 가능한 문자열을 만들어 주기 때문에
        // StringBuilder를 사용

        // 비동기 작업 시작
        // Future 객체는 비동기 작업의 결과를 저장
        Future<String> future = executor.submit(() -> {
            Process process = null;

            try {
                // 'javac'를 사용하여 Java 코드 컴파일
                //ProcessBuilder는 하나의 프로세스를 시작하기 위한 클래스
                ProcessBuilder processBuilder = new ProcessBuilder("javac", sourceFile.getAbsolutePath());
                process = processBuilder.start(); // start() 메서드는 새로운 프로세스를 시작

                // 컴파일 작업에 대한 5초 타임아웃 설정
                // process.waitFor() 메서드는 지정된 시간동안 프로세스의 완료를 기다림
                // 프로세스가 지정된 시간 내에 완료되지 않으면, if문이 실행
                if (!process.waitFor(5, TimeUnit.SECONDS)) {
                    //로그 기록하기,           {} 값에                             여기서부터의 값들이 순서대로 대입됨
                    compileAndRunLogger.error("{} {} - 컴파일 타임아웃: {} - {}", formattedNow, mid, "컴파일 작업이 지정된 시간 내에 완료되지 않음 ", uuidPath);
                    // 타임아웃 발생 시 프로세스 종료 및 예외 발생
                    process.destroy(); //프로세스 강제 종료
                    throw new RuntimeException("컴파일 타임아웃");
                }

                // 컴파일 오류 검사
                if (process.exitValue() != 0) { //exitValue()는 프로세스의 종료 코드를 반환. 0이 아닌 값은 오류
                    // 컴파일 오류 메시지 캡처 및 반환
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            output.append(line).append("\n");
                        }
                    }

                    //파일 경로 잘라내기
                    int compileErrorPath = output.lastIndexOf("error:");
                    String compileError = output.substring(compileErrorPath, output.length());
                    return "컴파일 오류:\n" + compileError;
                }

//////////////////////////////////////////////  코드실행  ////////////////////////////////////////////////////
                // 컴파일된 클래스 실행하는 부분
                processBuilder = new ProcessBuilder("java", "-cp", uuidPath, noExtensionFileName); // ProcessBuilder를 재선언하여 자바 코드를 실행
                process = processBuilder.start();
                long pid = process.pid();

                // 정상 실행 시 결과 값 저장
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null ) {
//                    while ((line = reader.readLine()) != null && process.waitFor(5, TimeUnit.SECONDS)) {
                        output.append(line).append("\n");
                        Thread.sleep(20);

                    }
//                    if (process.waitFor(5, TimeUnit.SECONDS)){
//                        process.destroy();
//                    }

                }

                // 오류 발생 시 결과 값 저장
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                    }
                }
//
//                 실행 작업에 대한 5초 타임아웃 설정
                if (!process.waitFor(5, TimeUnit.SECONDS)) {
                    compileAndRunLogger.error("{} {} - 실행 타임아웃: {} ", formattedNow, mid, uuidPath);
                    // 타임아웃 발생 시 프로세스 종료
                    process.destroy();
                    throw new RuntimeException("실행 타임아웃");
                }

            } catch (IOException e) {
                compileAndRunLogger.error("{} {} - 프로세스 시작 오류: {} - {}", formattedNow, mid, e.getMessage(), uuidPath, e);
                throw new RuntimeException("프로세스 시작 오류.", e);
            } catch (InterruptedException e) {
                compileAndRunLogger.error("{} {} - 프로세스 중단됨: {} - {} ", formattedNow, mid, e.getMessage(), uuidPath, e);
                if (process != null) {
                    process.destroy();
                }
                throw new RuntimeException("프로세스 중단됨.", e);
            }

            return output.toString();
        });

        try {
            // 비동기 작업 결과 대기 및 반환
            return future.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            // 타임아웃 발생 시 작업 취소 및 타임아웃 메시지 반환
            future.cancel(true);

            return  output.toString()+"\n실행 타임아웃: 코드 실행 시간이 5초를 초과했습니다.\n\n";
        } catch (Exception e) {
            compileAndRunLogger.error("{} {} - 오류: {} - {}", formattedNow, mid, e.getMessage(), uuidPath, e);
            // 실행 중 발생한 오류 메시지 반환
            return "실행 오류: " + e.getMessage();
        } finally {
            compileAndRunLogger.info("{} {} - 컴파일 및 실행 완료 - {}", formattedNow, mid, uuidPath);
            // ExecutorService 종료
            executor.shutdownNow();
        }
    }



    //악의적인 코드 포함되어있는지 체크
    public static boolean chkCode(String code) {
        String[] patterns = {
                "System\\.exit",
                "Runtime\\.getRuntime\\(\\)\\.exec",
                "java\\.io\\.",
//                "import java.io.*;"
        };

        for (String patternStr : patterns) {
            Pattern pattern = Pattern.compile(patternStr);
            Matcher matcher = pattern.matcher(code);
            if (matcher.find()) {
                return false; //
            }
        }
        return true; //
    }
}
