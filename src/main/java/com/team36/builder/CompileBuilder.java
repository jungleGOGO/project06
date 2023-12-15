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
import java.util.UUID;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CompileBuilder {
    // private final String path = CompilerApplication.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    private final String path = "/Users/juncheol/Desktop/compile/";
//    private final String path = "D:\\hk\\project\\compile\\";

    // 파일명이랑 코드내용 전달 받음
    // 파일명 .java 아니면 alert띄우도록 프론트에서 처리
    // 메인메소드가 있어야 실행 가능

    //- 타임아웃
    //- 명령 제한
    //- 에러메시지 경로 표시


    private static final Logger logger = LoggerFactory.getLogger(CompileBuilder.class);

    public String compileAndRunCode(String code) {
        // 고유 식별자 생성하여 각 코드 실행을 고유한 디렉토리에 저장
        String uuid = UUID.randomUUID().toString();
        String uuidPath = path + uuid + "/";
        File newFolder = new File(uuidPath);

///////////////////////////////////////////// 자바 파일 생성 /////////////////////////////////////////////////////
        // 디렉토리 생성 시도
        if (!newFolder.mkdir()) {
            // 디렉토리 생성 실패 시 로그 기록 및 에러 메시지 반환
            logger.error("디렉토리 생성 오류: " + uuidPath);
            return "디렉토리를 생성할 수 없습니다.";
        }
        // 새 Java 소스 파일 생성
        File sourceFile = new File(uuidPath + "Test.java");
        // 제공된 코드를 파일에 쓰기
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(sourceFile))) {
            writer.write(code);
        } catch (IOException e) {
            // 파일 쓰기 실패 시 로그 기록 및 에러 메시지 반환
            logger.error("소스 코드 파일 작성 오류: " + e.getMessage());
            return "소스 코드 파일을 작성할 수 없습니다.";
        }

        System.out.println("---- 자바 파일 생성 완료 ----");
/////////////////////////////////////////////////  컴파일  ///////////////////////////////////////////////////////
        // 비동기 작업 실행을 위한 ExecutorService 생성
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // 비동기 작업 시작
        Future<String> future = executor.submit(() -> {
            Process process = null;
            StringBuilder output = new StringBuilder();

            try {
                // 'javac'를 사용하여 Java 코드 컴파일
                ProcessBuilder processBuilder = new ProcessBuilder("javac", sourceFile.getAbsolutePath());
                process = processBuilder.start();

                // 컴파일 작업에 대한 10초 타임아웃 설정
                if (!process.waitFor(10, TimeUnit.SECONDS)) {
                    // 타임아웃 발생 시 프로세스 종료 및 예외 발생
                    process.destroy();
                    throw new RuntimeException("컴파일 타임아웃");
                }

                // 컴파일 오류 검사
                if (process.exitValue() != 0) {
                    // 컴파일 오류 메시지 캡처 및 반환
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            output.append(line).append("\n");
                        }
                    }
                    return "컴파일 오류:\n" + output;
                }
                System.out.println("---- 자바 파일 컴파일 완료 ----");
//////////////////////////////////////////////  코드실행  ////////////////////////////////////////////////////
                // 컴파일된 클래스 실행
                processBuilder = new ProcessBuilder("java", "-cp", uuidPath, "Test");
                process = processBuilder.start();
                System.out.println("---- 프로세스 실행 시작 ---- " + process.isAlive());

                // 표준 출력 스트림 캡처
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    System.out.println();
                    while ((line = reader.readLine()) != null && process.waitFor(5, TimeUnit.SECONDS)) {
                        output.append(line).append("\n");
                    }
                    System.out.println("inputStream , 프로세스 상태 " + process.isAlive());

                }

                // 표준 오류 스트림 캡처
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                    }
                    System.out.println("ErrorStream , 프로세스 상태 " + process.isAlive());
                }
//
//                 실행 작업에 대한 5초 타임아웃 설정
                if (!process.waitFor(5, TimeUnit.SECONDS)) {
                    // 타임아웃 발생 시 프로세스 종료
                    process.destroy();
                    throw new RuntimeException("실행 타임아웃");
                }
                System.out.println("프로세스 상태 확인: " + process.isAlive());

            } catch (IOException e) {
                throw new RuntimeException("프로세스 시작 오류.", e);
            } catch (InterruptedException e) {
                if (process != null) {
                    process.destroy();
                }
                throw new RuntimeException("프로세스 중단됨.", e);
            } finally {
                process.destroyForcibly();
                System.out.println("finally , 프로세스 상태 " + process.isAlive());

            }
            System.out.println("try 종료 , 프로세스 상태 " + process.isAlive());

            return output.toString();
        });

        try {
            // 비동기 작업 결과 대기 및 반환
            return future.get(10, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            // 타임아웃 발생 시 작업 취소 및 타임아웃 메시지 반환
            future.cancel(true);

            return "실행 타임아웃: 코드 실행 시간이 5초를 초과했습니다.";
        } catch (Exception e) {
            // 실행 중 발생한 오류 메시지 반환
            return "실행 오류: " + e.getMessage();
        } finally {
            // ExecutorService 종료
            executor.shutdownNow();
        }
    }
}


//    @SuppressWarnings({ "resource", "deprecation" })
//    public Object compileCode(String body) throws Exception {
//        String uuid = UUIDUtil.createUUID();
//        String uuidPath = path + uuid + "/";
//
//        // Source를 이용한 java file 생성
//        File newFolder = new File(uuidPath);
//        File sourceFile = new File(uuidPath + "Test.java");
//        File classFile = new File(uuidPath + "Test.class");
//
//        Class<?> cls = null;
//
//        // compile System err console 조회용 변수
//        ByteArrayOutputStream err = new ByteArrayOutputStream();
//        PrintStream origErr = System.err;
//
//        System.out.println("sourceFile : "+sourceFile);
//        try {
//            newFolder.mkdir();
//            try (BufferedWriter writer = new BufferedWriter(new FileWriter(sourceFile))) {
//                writer.write(body);
//            }
//            // 만들어진 Java 파일을 컴파일
//            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
//
//            // System의 error outputStream을 ByteArrayOutputStream으로 받아오도록 설정
//            System.setErr(new PrintStream(err));
//
//            // compile 진행
//            int compileResult = compiler.run(null, null, null, sourceFile.getPath());
//            System.out.println("compileResult : "+compileResult);
//            // compile 실패인 경우 에러 로그 반환
//            if(compileResult == 1) {
//                System.out.println("컴파일 실패");
//                return err.toString();
//            }
//
//            // 컴파일된 Class를 Load
//            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] {new File(uuidPath).toURI().toURL()});
//            cls = Class.forName("Test", true, classLoader);
//
//            // Load한 Class의 Instance를 생성
//            return cls.newInstance();
//        } catch (Exception e) {
//            log.error("[CompileBuilder] 소스 컴파일 중 에러 발생 :: {}", e.getMessage());
//            e.printStackTrace();
//            return null;
//        } finally {
//            // Syetem error stream 원상태로 전환
//            System.setErr(origErr);
//
//            if(sourceFile.exists())
//                sourceFile.delete();
//            if(classFile.exists())
//                classFile.delete();
//            if(newFolder.exists())
//                newFolder.delete();
//        }
//    }
//
//    @SuppressWarnings({ "rawtypes", "unchecked" })
////    public Map<String, Object> runObject(Object obj, Object[] params) throws Exception {
//    public Map<String, Object> runObject(Object obj) throws Exception {
//        Map<String, Object> returnMap = new HashMap<String, Object>();
//
//
//
//        // 실행할 메소드 명
//        String methodName = "main";
////        String methodName = "runMethod";
////        String className = "Test";
//        // 파라미터 타입 개수만큼 지정
////        Class arguments[] = new Class[params.length];
////        for(int i = 0; i < params.length; i++)
////            arguments[i] = params[i].getClass();
//
//        /*
//         * reflection method의 console output stream을 받아오기 위한 변수
//         * reflection method 실행 시 System의 out, error outputStream을 ByteArrayOutputStream으로 받아오도록 설정
//         * 실행 완료 후 다시 원래 System으로 전환
//         */
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        ByteArrayOutputStream err = new ByteArrayOutputStream();
//        PrintStream origOut = System.out;
//        PrintStream origErr = System.err;
//        try {
//            // System의 out, error outputStream을 ByteArrayOutputStream으로 받아오도록 설정
//            System.setOut(new PrintStream(out));
//            System.setErr(new PrintStream(err));
//
//            // 메소드 timeout을 체크하며 실행(15초 초과 시 강제종료)
//            Map<String, Object> result = new HashMap<String, Object>();
//
//            result = MethodExecutation.timeOutCall(obj, methodName, params, arguments);
//
//            // stream 정보 저장
//            if((Boolean) result.get("result")) {
//                returnMap.put("result", ApiResponseResult.SUCEESS.getText());
//                returnMap.put("return", result.get("return"));
//                if(err.toString() != null && !err.toString().equals("")) {
//                    returnMap.put("SystemOut", err.toString());
//                }else {
//                    returnMap.put("SystemOut", out.toString());
//                }
//            }else {
//                returnMap.put("result", ApiResponseResult.FAIL.getText());
//                if(err.toString() != null && !err.toString().equals("")) {
//                    returnMap.put("SystemOut", err.toString());
//                }else {
//                    returnMap.put("SystemOut", "제한 시간 초과");
//                }
//            }
//        }catch (Exception e) {
//            e.printStackTrace();
//        }finally {
//            // Syetem out, error stream 원상태로 전환
//            System.setOut(origOut);
//            System.setErr(origErr);
//        }
//        System.out.println("returnMap : "+returnMap);
//        return returnMap;
//    }

