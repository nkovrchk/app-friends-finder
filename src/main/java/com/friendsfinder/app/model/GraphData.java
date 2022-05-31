package com.friendsfinder.app.model;

import lombok.Data;

import java.util.*;

@Data
public class GraphData {
    private final Integer width;

    private final Integer depth;

    private final List<List<String>> words = new ArrayList<>();

    private final Set<Integer> uniqueIds = new HashSet<>();

    private final List<MatchData> matches = new ArrayList<>();


    public void addAllIds(Collection<Integer> ids) {
        this.uniqueIds.addAll(ids);
    }
}
