package com.friendsfinder.app.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MatchData {
    Double total;

    MatchSection info;

    MatchSection wall;

    MatchSection groups;

    Integer userId;

    @Data
    public static class MatchSection {

        ArrayList<String> w;

        Double r;
    }
}
