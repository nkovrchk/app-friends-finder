package com.friendsfinder.app.model;

import lombok.Data;

import java.util.ArrayList;

@Data
public class UserData {
    private final int id;

    private String firstName;

    private String lastName;

    private String career;

    private String about;

    private String interests;

    private String city;

    private ArrayList<String> wall = new ArrayList<>();

    public UserData (VKUser user) {
        this.id = user.getId();
    }

    public void refreshInfo () {

    }
}
