package com.aloa.common.video.repository;

import com.aloa.common.video.entity.VideoHist;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoHistRepository extends JpaRepository<VideoHist, Long> {
    Optional<VideoHist> findFirstByPathOrderByHistSequenceDesc(@NonNull String path);
}
