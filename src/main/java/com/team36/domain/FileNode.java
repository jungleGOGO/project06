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



    public FileNode(String name, String text) {
        this.name = name;
        this.text = text;
        this.children = new ArrayList<>();
//        System.out.println("text : "+text);
        this.flagUrl = determineFlagUrl("/Users/juncheol/Desktop/storage"+text);
    }

    public void addChild(FileNode child) {
        this.children.add(child);
    }
    private String determineFlagUrl(String name) {
//        System.out.println(name);
        if (new File(name).isDirectory()) {
            System.out.println("dir => " + name);
            return "/static/img/folder.svg";
        } else {
            System.out.println("file => " + name);
            return "/static/img/file.svg";
        }
    }
}