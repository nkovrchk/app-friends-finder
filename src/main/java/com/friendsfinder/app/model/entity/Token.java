package com.friendsfinder.app.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity(name = "Token")
@Table(name = "token")
public class Token extends BaseEntity {
    @Id
    @Column(name = "user_id")
    @Getter
    @Setter
    private Integer userId;

    @Column(name = "access_token")
    @Getter
    @Setter
    private String accessToken;

    @Column(name = "expires_in")
    @Getter
    @Setter
    private int expiresIn;

    @Column(name = "creation_date")
    @Getter
    @Setter
    private Date creationDate;

}
