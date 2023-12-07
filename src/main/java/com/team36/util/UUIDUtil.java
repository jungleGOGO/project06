package com.team36.util;

import java.util.UUID;

public class UUIDUtil {
    public static String createUUID() {
        String uuid = UUID.randomUUID().toString().replace("-","");
        return uuid;
    }
}
