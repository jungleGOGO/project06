package com.team36.util;

import java.util.UUID;

public class UUIDUtil {
    public static String createUUID() {
        String uuid = UUID.randomUUID().toString().replace("-","");
        return uuid;
    }

//    public static void main(String[] args) {
//
//        int i=0;
//        while(true){
//            i++;
//            System.out.println(i);
//        }
//    }

}
