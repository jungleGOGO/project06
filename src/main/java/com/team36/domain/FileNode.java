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
        this.flagUrl = determineFlagUrl("/Users/juncheol/Desktop/storage/"+mid+text);
//        this.flagUrl = determineFlagUrl("Y:\\team36\\"+text);
//        this.flagUrl = determineFlagUrl("\\\\10.41.0.153\\team36\\"+mid+text);

    }

    public void addChild(FileNode child) {
        this.children.add(child);
    }
    private String determineFlagUrl(String name) {
        if (new File(name).isDirectory()) {
            return "/static/img/icon/folder.svg";
        } else {
            return "/static/img/icon/file.svg";
        }
    }
}