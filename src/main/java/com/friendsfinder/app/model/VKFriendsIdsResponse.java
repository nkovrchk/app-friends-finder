package com.friendsfinder.app.model;

import lombok.Data;

import java.util.ArrayList;

@Data
public class VKFriendsIdsResponse {
    public VKFriendsIds response;

    @Data
    public static class VKFriendsIds {
        public int count;

        public ArrayList<Integer> items;
    }
}
