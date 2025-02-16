package com.aloa.common.video.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class VideoValidator {
    private final VideoFinder videoFinder;

    public boolean isDuplicated(String path) {

        //유튜브경로로 이미 유튜브가 등록되어있는지 검색
        var videoByPath = videoFinder.findByPath(path);

        if(videoByPath.isPresent()) return true;
        var youtubeVideoId = extractVideoId(path);

        var videoByYoutubeVideoId = videoFinder.findByYoutubeVideoId(youtubeVideoId);

        return videoByYoutubeVideoId.isPresent();
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
