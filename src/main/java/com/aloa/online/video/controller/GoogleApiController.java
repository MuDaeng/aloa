package com.aloa.online.video.controller;

import com.aloa.common.video.entity.CalculationState;
import com.aloa.common.video.entity.Video;
import com.aloa.common.video.manager.GoogleApiManager;
import com.aloa.common.video.manager.VideoSaveManager;
import com.aloa.online.video.dto.PathDTO;
import com.google.api.services.youtube.model.VideoSnippet;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/youtube/v1")
public class GoogleApiController {
    private final GoogleApiManager googleApiManager;
    private final VideoSaveManager videoSaveManager;

    @GetMapping("youtube")
    public VideoSnippet getUri(@RequestParam String path){

        VideoSnippet result = googleApiManager.getYoutubeSnippet(path);

        var a = result.getPublishedAt();
        var b = a.toStringRfc3339().replace("Z", "");

        return result;
    }

    @PostMapping("/reg-video")
    public Video regVideo(@RequestBody PathDTO pathDTO){
        var youtubeInfo = googleApiManager.getYoutubeInfo(pathDTO.getPath());
        youtubeInfo.setCalculationState(CalculationState.WAITING);
        return videoSaveManager.regVideo(youtubeInfo);
    }
}
