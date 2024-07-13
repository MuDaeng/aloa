package com.aloa.common.video.validator;

import com.aloa.common.video.entity.CalculationState;
import com.aloa.common.video.entity.Video;
import com.aloa.common.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class VideoValidator {
    private final VideoRepository videoRepository;

    public boolean isDuplicated(String path) {

        //유튜브경로로 이미 유튜브가 등록되어있는지 검색
        var videoByPath = videoRepository.findByPath(path);

        if(videoByPath.isPresent()) return true;
        var youtubeVideoId = extractVideoId(path);

        var videoByYoutubeVideoId = videoRepository.findByYoutubeVideoId(youtubeVideoId);

        return videoByYoutubeVideoId.isPresent();
    }

    public CalculationState getCalculationState(Optional<Video> video) {
        return video.map(Video::getCalculationState)
                .orElseThrow(
                        () -> new IllegalArgumentException("등록된 비디오가 없습니다."))
                ;
    }

    public String extractVideoId(String path){
        String[] split = path.split("\\?");

        var videoId = Stream.of(split)
                .filter(str -> str.contains("v="))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 URL을 입력했습니다."))
                .replace("v=", "");

        if(videoId.contains("&")){
            videoId = videoId.substring(0, videoId.indexOf("&"));
        }

        return videoId;
    }
}
