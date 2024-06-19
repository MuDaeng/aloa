package com.aloa.online.video.controller;

import com.aloa.common.video.manager.YoutubeFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/youtube/v1")
public class GoogleApiController {
    private final YoutubeFeignClient youtubeFeignClient;

    @GetMapping("youtube")
    public Map<String, String> getUri(@RequestParam String path){
        return youtubeFeignClient.getYoutubeInfo(path);
    }
}
