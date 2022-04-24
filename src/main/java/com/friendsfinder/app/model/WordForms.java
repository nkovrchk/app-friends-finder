package com.friendsfinder.app.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class WordForms {
    private List<String> info = new ArrayList<>();

    private List<String> wall = new ArrayList<>();

    private List<String> groups = new ArrayList<>();
}
