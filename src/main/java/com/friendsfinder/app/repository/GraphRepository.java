package com.friendsfinder.app.repository;

import com.friendsfinder.app.model.entity.Graph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GraphRepository extends JpaRepository<Graph, Integer> { }
