package com.aloa.online.video.validator;

import com.aloa.common.video.entity.CalculationState;
import com.aloa.common.video.entity.Video;
import com.aloa.common.video.manager.GoogleApiManager;
import com.aloa.common.video.repository.VideoRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
@RequiredArgsConstructor
public class VideoValidator {
    private final VideoRepository videoRepository;
    private final GoogleApiManager googleApiManager;

    public boolean isDuplicated(String path) {
        //유튜브경로로 이미 유튜브가 등록되어있는지 검색
        var video = videoRepository.findByPath(path);

        if(video.isPresent()) throw new IllegalArgumentException(video.get().getCalculationState().getMessage());

        googleApiManager.getYoutubeInfo(path);
        return false;
    }

    public CalculationState getCalculationState(Optional<Video> video) {
        return video.map(Video::getCalculationState)
                .orElseThrow(
                        () -> new IllegalArgumentException("등록된 비디오가 없습니다."))
                ;
    }

    public Optional<Video> findByPath(@NonNull String path) {
        return videoRepository.findByPath(path);
    }
}
