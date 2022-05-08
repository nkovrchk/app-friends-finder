package com.friendsfinder.app.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

@Data
public class UserGraph {
    private ArrayList<ArrayList<ArrayList<Node>>> graph;

    private int width;

    private int depth;

    private Map<Integer, MatchData> matchData;

    private Set<Integer> uniqueIds;
}
