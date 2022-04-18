package com.friendsfinder.app.repository;

import com.friendsfinder.app.model.entity.UserGraph;
import org.springframework.data.repository.CrudRepository;

public interface GraphRepository extends CrudRepository<UserGraph, Long> {
}
