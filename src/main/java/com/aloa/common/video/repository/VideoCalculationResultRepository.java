package com.aloa.common.video.repository;

import com.aloa.common.video.entity.VideoCalculationResult;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoCalculationResultRepository extends JpaRepository<VideoCalculationResult, Long> {
    List<VideoCalculationResult> findByVideoId(@NonNull Long videoId);
}
