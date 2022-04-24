package com.friendsfinder.app.model.entity;

import org.springframework.data.annotation.*;
import org.springframework.data.annotation.Version;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;

@MappedSuperclass
public class BaseEntity {
    /**
     * Дата создания записи в таблице
     */
    @CreatedDate
    @ReadOnlyProperty
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    /**
     * Дата обновления записи в таблице
     */
    @LastModifiedDate
    @ReadOnlyProperty
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn;

    @PrePersist
    protected void preCreate() {
        this.createdOn = Timestamp.from(Instant.now());
    }

    @PreUpdate
    protected void preUpdate () {
        this.updatedOn = Timestamp.from(Instant.now());
    }
}
