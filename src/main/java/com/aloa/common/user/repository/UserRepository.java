package com.aloa.common.user.repository;

import com.aloa.common.user.entitiy.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByName(String username);
    Optional<User> findByGoogleUserId(String googleUserId);
}
