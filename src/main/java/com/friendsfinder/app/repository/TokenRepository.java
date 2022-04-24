package com.friendsfinder.app.repository;

import com.friendsfinder.app.model.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Integer> { }
