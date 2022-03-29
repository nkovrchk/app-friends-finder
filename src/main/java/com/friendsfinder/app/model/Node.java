package com.friendsfinder.app.model;

import lombok.Data;

import java.util.Map;

@Data
public class Node {

    private final int depth;

    private final int id;

    private int parent;

    private VKUser user;

    private Map<String, String[]> wordForms;

    public Node (int depth, int id){
        this.depth = depth;
        this.id = id;
    }

    public Node (int depth, int id, int parent) {
        this.depth = depth;
        this.id = id;
        this.parent = parent;
    }
}
