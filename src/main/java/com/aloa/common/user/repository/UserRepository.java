package com.aloa.common.user.repository;

import com.aloa.common.user.entitiy.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {
    User findByGoogleAuthToken(String googleAuthToken);
    User findByGoogleUserId(Long googleUserId);
}
