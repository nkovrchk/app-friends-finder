package com.friendsfinder.app.model;

import lombok.Data;

@Data
public class Node {

    private final int depth;

    private final int userId;

    private int parentId;

    private User user;

    private WordForms wordForms = new WordForms();

    public Node (int depth, int userId){
        this.depth = depth;
        this.userId = userId;
    }
}
