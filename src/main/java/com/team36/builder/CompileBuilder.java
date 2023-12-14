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
        String uuid = UUID.randomUUID().toString();
        String uuidPath = path + uuid + "/";
        File newFolder = new File(uuidPath);
        if (!newFolder.mkdir()) {
            logger.error("Error creating directory: " + uuidPath);
            return "Error creating directory.";
        }

        File sourceFile = new File(uuidPath + "Test.java");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(sourceFile))) {
            writer.write(code);
        } catch (IOException e) {
            logger.error("Error writing the source code to file: " + e.getMessage());
            return "Error writing the source code to file.";
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();

        Future<String> future = executor.submit(() -> {
            Process process = null;

            StringBuilder output = new StringBuilder();
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("javac", sourceFile.getAbsolutePath());
                process = processBuilder.start();
                if (!process.waitFor(10, TimeUnit.SECONDS)) {
                    process.destroy();
                    throw new RuntimeException("Compilation timeout");
                }

                if (process.exitValue() != 0) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            output.append(line).append("\n");
                        }
                    }
                    return "Compilation error:\n" + output;
                }

                processBuilder = new ProcessBuilder("java", "-cp", uuidPath, "Test");
                process = processBuilder.start();
                System.out.println("process alive "+process.isAlive());
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;

                    while ((line = reader.readLine()) != null && process.waitFor(5,TimeUnit.SECONDS)) {
                        output.append(line).append("\n");
//                        if (process.waitFor(10,TimeUnit.SECONDS)){
//                            process.destroy();
//                        }
                        System.out.println("while 끝 process alive "+process.isAlive());
                    }

                }
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                    }
                }

                if (!process.waitFor(5, TimeUnit.SECONDS)) {
                    process.destroy();
                    throw new RuntimeException("Execution timeout");
                }
                System.out.println("check "+process.isAlive());

            } catch (IOException e) {
                throw new RuntimeException("Error starting the process.", e);
            } catch (InterruptedException e) {
                if (process != null) {
                    process.destroy();
                }
                throw new RuntimeException("Process was interrupted.", e);
            }
            System.out.println("---------------------false면 종료-------------");
            System.out.println(process.isAlive());
            return output.toString();
        });

        try {
            return future.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);

            return "Execution timeout: Code execution took longer than 5 seconds.";
        } catch (Exception e) {
            return "Execution error: " + e.getMessage();
        } finally {
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

