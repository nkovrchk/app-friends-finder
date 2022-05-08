package com.friendsfinder.app.controller.dto.request;

import lombok.Data;

import java.util.ArrayList;

@Data
public class SearchRequest {
    private int depth;

    private int width;

    private ArrayList<String> keyWords;
}
