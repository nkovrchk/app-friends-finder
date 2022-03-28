package com.friendsfinder.app.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.util.Date;

public class AccessToken {
    @Getter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
    private Date createdOn;
    @Getter
    private int expiresIn;
    @Getter
    private String token;
    @Getter
    private int userId;

    public AccessToken(){
        super();
    }

    public AccessToken(Date createdOn, int expiresIn, String token, int userId){
        this.createdOn = createdOn;
        this.expiresIn = expiresIn;
        this.token = token;
        this.userId = userId;
    }
}
