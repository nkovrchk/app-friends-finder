package com.friendsfinder.app.model.entity;

import org.springframework.data.annotation.*;

import javax.persistence.MappedSuperclass;
import java.time.Instant;

@MappedSuperclass
public class BaseEntity {
    @Version
    private Integer version;

    @CreatedDate
    @ReadOnlyProperty
    private Instant createdOn;

    @CreatedBy
    @ReadOnlyProperty
    private String createdBy;

    @LastModifiedDate
    @ReadOnlyProperty
    private Instant updatedOn;

    @LastModifiedBy
    @ReadOnlyProperty
    private String updatedBy;

}
