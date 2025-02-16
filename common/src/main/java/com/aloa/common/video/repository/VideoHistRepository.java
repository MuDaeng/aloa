package com.aloa.common.video.repository;

import com.aloa.common.video.entity.VideoHist;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface VideoHistRepository extends JpaRepository<VideoHist, Long> {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    Optional<VideoHist> findFirstByPathOrderByHistSequenceDesc(@NonNull String path);
}
