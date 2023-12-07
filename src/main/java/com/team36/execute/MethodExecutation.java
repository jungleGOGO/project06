package com.team36.execute;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class MethodExecutation {
    private final static long TIMEOUT_LONG = 15000; // 15초

    public static Map<String, Object> timeOutCall(Object obj, String methodName, Object[] params, Class<? extends Object> arguments[]) throws Exception {
        // return Map
        Map<String, Object> returnMap = new HashMap<String, Object>();

        // Source를 만들때 지정한 Method
        Method objMethod;
        // 매개변수 타입 속성 개수가 1개인 경우
        if(arguments.length == 1)
            objMethod = obj.getClass().getMethod(methodName, arguments[0]);
            // 매개변수 타입 속성 개수가 2개인 경우
        else if(arguments.length == 2)
            objMethod = obj.getClass().getMethod(methodName, arguments[0], arguments[1]);
            // 그 외
        else
            objMethod = obj.getClass().getMethod(methodName);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Callable<Map<String, Object>> task = new Callable<Map<String, Object>>() {
            @Override
            public Map<String, Object> call() throws Exception {
                Map<String, Object> callMap = new HashMap<String, Object>();

                // 아래 주석 해제시 timeout 테스트 가능
                // Thread.sleep(4000);

                // Method 실행
                // 파라미터 개수가 1개인 경우 1개 등록
                if(params.length == 1)
                    callMap.put("return", objMethod.invoke(obj, new Object[] {params}));
                    // 파라미터 개수가 2개 이상인 경우 2개까지 등록
                else if(params.length == 2)
                    callMap.put("return", objMethod.invoke(obj, params[0], params[1]));
                    // 그 외
                else
                    callMap.put("return", objMethod.invoke(obj));

                callMap.put("result", true);
                return callMap;
            }
        };

        Future<Map<String, Object>> future = executorService.submit(task);
        try {
            // 타임아웃 감시할 작업 실행
            returnMap = future.get(TIMEOUT_LONG, TimeUnit.MILLISECONDS); // timeout을 설정
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            // e.printStackTrace();
            returnMap.put("result", false);
        } finally {
            executorService.shutdown();
        }

        return returnMap;
    }
}
