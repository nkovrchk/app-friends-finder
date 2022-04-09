package com.friendsfinder.app.model;

import com.friendsfinder.app.service.vk.dto.VKUser;
import lombok.Data;

import java.util.Map;

@Data
public class Node {

    private final int depth;

    private final int userId;

    private int parent;

    private VKUser user;

    private Map<String, String[]> wordForms;

    public Node (int depth, int userId){
        this.depth = depth;
        this.userId = userId;
    }

    public Node (int depth, int userId, int parent) {
        this.depth = depth;
        this.userId = userId;
        this.parent = parent;
    }
}
