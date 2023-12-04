package com.tcoding.team36.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FileNode {
    private String name;
    private String text;
    private List<FileNode> children;


    public FileNode(String name, String text) {
        this.name = name;
        this.text = text;
        this.children = new ArrayList<>();
    }

    public void addChild(FileNode child) {
        this.children.add(child);
    }
}
