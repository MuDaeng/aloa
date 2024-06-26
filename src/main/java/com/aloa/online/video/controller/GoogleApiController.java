package com.aloa.online.video.controller;

import com.aloa.common.video.manager.GoogleApiManager;
import com.google.api.services.youtube.model.VideoSnippet;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/youtube/v1")
public class GoogleApiController {
    private final GoogleApiManager googleApiManager;

    @GetMapping("youtube")
    public VideoSnippet getUri(@RequestParam String path){

        VideoSnippet result = googleApiManager.getYoutubeSnippet(path);

        var a = result.getPublishedAt();
        var b = a.toStringRfc3339().replace("Z", "");

        LocalDateTime c = LocalDateTime.parse(b);

        return result;
    }
}
