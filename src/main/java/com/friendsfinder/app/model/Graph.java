package com.friendsfinder.app.model;

import lombok.Data;

import java.util.ArrayList;

@Data
public class Graph {
    private ArrayList<ArrayList<ArrayList<Node>>> graph;

    private int width;

    private int depth;

    private int rootId;

    public Graph(ArrayList<ArrayList<ArrayList<Node>>> graph, int width, int depth){
        this.graph = graph;
        this.width = width;
        this.depth = depth;
    }
}
