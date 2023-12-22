package com.team36;

import com.team36.util.CompressZip;

public class ZipTest {
    public static void main(String[] args) {
        // 압축 파일 위치와 압축된 파일
        // String zipPath = "G:/ZIP_TEST/";
        // String zipFile = "jsmpeg-player-master.zip";

        // 압축을 해제할 위치, 압축할 파일이름
        String unZipPath = "\\\\10.41.0.153\\team36\\2\\java";
        String unZipFile = "zipTest";

        System.out.println("--------- 압축 하기 ---------");
        CompressZip compressZip = new CompressZip();

        // 압축 하기
        try {
            if (!compressZip.compress("\\\\10.41.0.153\\team36\\zip\\zipTest", unZipPath, unZipFile)) {
                System.out.println("압축 실패");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
