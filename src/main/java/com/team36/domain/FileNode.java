package com.team36.domain;

import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Data
public class FileNode {
    private String name;
    private String text;
    private List<FileNode> children;
    private String flagUrl;
    private String mid;



    public FileNode(String name, String text, String mid) {
        this.name = name;
        this.text = text;
        this.children = new ArrayList<>();
        this.mid = mid;
//        this.flagUrl = determineFlagUrl("D:\\kimleeho\\"+text);

        // TODO : 경로 수정
//        this.flagUrl = determineFlagUrl("/Users/juncheol/mounttest/"+mid+text);
//        this.flagUrl = determineFlagUrl("C:\\kimleeho\\savef\\"+mid+text);
//        this.flagUrl = determineFlagUrl("/Users/juncheol/Desktop/storage/"+text);
//        this.flagUrl = determineFlagUrl("Y:\\storage\\"+text);
        this.flagUrl = determineFlagUrl("\\\\10.41.0.153\\team36\\"+mid+text);

    }

    public void addChild(FileNode child) {
        this.children.add(child);
    }
    private String determineFlagUrl(String name) {
        if (new File(name).isDirectory()) {
            return "/static/img/icon/folder.svg";
        } else {
            // 파일인 경우 확장자 추출
            int lastDotIndex = name.lastIndexOf('.');
            if (lastDotIndex > 0) {
                String extension = name.substring(lastDotIndex + 1);
                // 각 확장자에 따라 다른 이미지 반환
                switch (extension.toLowerCase()) {
                    case "html":
                        return "/static/img/icon/html.svg";
                    case "css":
                        return "/static/img/icon/css.svg";
                    case "js":
                        return "/static/img/icon/javascript.svg";
                    default:
                        // 기본적으로 파일 아이콘 반환
                        return "/static/img/icon/file.svg";
                }
            }

            return "/static/img/icon/file.svg";
        }
    }
}