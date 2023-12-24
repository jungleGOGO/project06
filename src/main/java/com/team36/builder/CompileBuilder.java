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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CompileBuilder {
    // private final String path = CompilerApplication.class.getProtectionDomain().getCodeSource().getLocation().getPath();

    // TODO : 경로 수정
    private final String path = "/Users/juncheol/Desktop/compile/";
//    private final String path = "D:\\hk\\project\\compile\\";
//    private final String path = "D:\\compile\\";

    // 파일명이랑 코드내용 전달 받음
    // 파일명 .java 아니면 alert띄우도록 프론트에서 처리
    // 메인메소드가 있어야 실행 가능

    //- 타임아웃
    //- 명령 제한
    //- 에러메시지 경로 표시


    private static final Logger logger = LoggerFactory.getLogger(CompileBuilder.class);

    //전달받은 코드 컴파일하고 실행 결과 반환
    public String compileAndRunCode(String code, String fileName) {
//        System.out.println(code);
//        System.out.println("fileName"+fileName);
        int dotIndex = fileName.lastIndexOf(".");
        String noExtensionFileName = fileName.substring(0, dotIndex); // 파일 확장자 잘라내기

        // 악의적인 코드 유무 확인
        if (!chkCode(code)) {
            return "실행할 수 없는 코드입니다.";
        }

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
        File sourceFile = new File(uuidPath + fileName);
        // 제공된 코드를 파일에 쓰기
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(sourceFile))) {
            writer.write(code);
        } catch (IOException e) {
            // 파일 쓰기 실패 시 로그 기록 및 에러 메시지 반환
            logger.error("소스 코드 파일 작성 오류: " + e.getMessage());
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
                    // 타임아웃 발생 시 프로세스 종료
                    process.destroy();
                    throw new RuntimeException("실행 타임아웃");
                }

            } catch (IOException e) {
                throw new RuntimeException("프로세스 시작 오류.", e);
            } catch (InterruptedException e) {
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
            // 실행 중 발생한 오류 메시지 반환
            return "실행 오류: " + e.getMessage();
        } finally {
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

