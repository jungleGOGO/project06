package com.team36.builder;

import com.team36.util.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

@Slf4j
@Component
public class CompileBuilder {
    // private final String path = CompilerApplication.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    private final String path = "/Users/juncheol/Desktop/compile/";
//    private final String path = "D:\\hk\\project\\compile\\";


    public String compileAndRunCode(String code) throws Exception {
        // 유일한 식별자 생성
        String uuid = UUIDUtil.createUUID();
        // 코드를 저장할 고유 경로 생성
        String uuidPath = path + uuid + "/";
        File newFolder = new File(uuidPath);
        // Java 파일 생성
        File sourceFile = new File(uuidPath + "Test.java");
        // 여기에서 Java 파일의 이름을 전달받아 처리

        // 디렉토리 생성
        newFolder.mkdir();

        // 제공된 코드를 파일에 작성
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(sourceFile))) {
            writer.write(code);
        }

        // Java 컴파일러 가져오기
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        // 컴파일 오류를 캡처하기 위한 출력 스트림
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        // 컴파일 실행, 오류가 있으면 err 스트림에 저장
        int compileResult = compiler.run(null, null, new PrintStream(err), sourceFile.getPath());

        // 컴파일 오류 체크
        if (compileResult != 0) {
            return "Compile error: " + err.toString();
        }

        // 컴파일된 클래스 로드
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{new File(uuidPath).toURI().toURL()});
        // 클래스명을 파일명과 일치시키기 위한 로직 필요
        Class<?> cls = Class.forName("Test", true, classLoader);

        // 실행 결과를 캡처하기 위한 출력 스트림
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));

        // 클래스의 main 메소드 실행
        try {
            Method method = cls.getMethod("main", String[].class);
            String[] params = null; // main 메소드에 전달할 파라미터
            method.invoke(null, (Object) params); // static 메소드 호출
        } catch (Exception e) {
            // 오류 발생 시 기존 System.out 스트림으로 복원
            System.setOut(originalOut);
            e.printStackTrace();
            return "Execution error: " + e.getMessage();
        } finally {
            // 스트림 복원
            System.setOut(originalOut);
        }

        // 실행 결과 반환
        return out.toString();
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

