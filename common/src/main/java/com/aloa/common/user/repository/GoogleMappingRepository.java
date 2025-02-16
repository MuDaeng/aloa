package com.aloa.common.user.repository;

import com.aloa.common.user.entitiy.GoogleMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GoogleMappingRepository extends JpaRepository<GoogleMapping, Long> {
    Optional<GoogleMapping> findByGoogleUserId(String googleUserId);
}
