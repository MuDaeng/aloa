package com.aloa.common.video.repository;

import com.aloa.common.video.entity.VideoMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoMappingRepository extends JpaRepository<VideoMapping, Integer> {
    Optional<VideoMapping> findByVideoId(Long id);
}
