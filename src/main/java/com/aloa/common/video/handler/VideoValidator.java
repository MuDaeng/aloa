package com.aloa.common.video.handler;

import com.aloa.common.user.repository.GoogleMappingRepository;
import com.aloa.common.util.SignedInUserUtil;
import com.aloa.common.video.entity.CalculationState;
import com.aloa.common.video.entity.Video;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class VideoValidator {
    private final VideoFinder videoFinder;
    private final GoogleMappingRepository googleMappingRepository;

    public boolean isDuplicated(String path) {

        //유튜브경로로 이미 유튜브가 등록되어있는지 검색
        var videoByPath = videoFinder.findByPath(path);

        if(videoByPath.isPresent()) return true;
        var youtubeVideoId = extractVideoId(path);

        var videoByYoutubeVideoId = videoFinder.findByYoutubeVideoId(youtubeVideoId);

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

    public boolean isVideoOfUser(@NonNull String channelId){
        var signedInUser = SignedInUserUtil.getSignedInUser();

        if(!signedInUser.isSignedIn()) return false;

        Optional.of(channelId)
                .filter(str -> !str.isBlank())
                .orElseThrow(() -> new IllegalArgumentException("video path is null"));

        String googleEmail = signedInUser.getUserId();

        return googleMappingRepository.findByGoogleUserId(googleEmail).filter(googleMapping -> channelId.equals(googleMapping.getChannelId())).isPresent();
    }
}
