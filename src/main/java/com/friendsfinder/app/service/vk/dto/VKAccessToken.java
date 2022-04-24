package com.friendsfinder.app.service.vk.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.util.Date;

public class VKAccessToken {
    @Getter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
    private Date createdOn;
    @Getter
    private int expiresIn;
    @Getter
    private String token;
    @Getter
    private int userId;

    public VKAccessToken(){
        super();
    }

    public VKAccessToken(Date createdOn, int expiresIn, String token, int userId){
        this.createdOn = createdOn;
        this.expiresIn = expiresIn;
        this.token = token;
        this.userId = userId;
    }
}
