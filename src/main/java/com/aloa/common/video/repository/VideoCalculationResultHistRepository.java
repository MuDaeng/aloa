package com.aloa.common.video.repository;

import com.aloa.common.video.entity.VideoCalculationResultHist;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoCalculationResultHistRepository extends JpaRepository<VideoCalculationResultHist, Long> {
    List<VideoCalculationResultHist> findTop16ByVideoIdOrderByCardAscSequenceDesc(@NonNull Long videoId);
}
