package com.aloa.common.video.repository;

import com.aloa.common.video.entity.Video;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, Long> {
    Optional<Video> findByPath(String path);

    List<Video> findByChosungStartsWith(String chosung);

    List<Video> findByTitleStartsWith(String title);

    List<Video> findByExpeditionId(@NonNull Long expeditionId);

    List<Video> findByExpeditionIdAndCharacterSequence(@NonNull Long expeditionId, @NonNull Integer characterSequence);
}
