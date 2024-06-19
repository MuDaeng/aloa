package com.aloa.common.video.manager;

import com.aloa.common.video.entity.Video;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GoogleApiManager {
    private final YoutubeFeignClient youtubeFeignClient;

    public Video getYoutubeInfo(@NonNull String path){
        var video = youtubeFeignClient.getYoutubeInfo(path);

        return new Video();
    }
}
