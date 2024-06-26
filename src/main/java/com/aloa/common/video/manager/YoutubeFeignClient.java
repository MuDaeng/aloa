package com.aloa.common.video.manager;

import com.google.api.services.youtube.YouTube;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "YouTube", url = "https://www.googleapis.com/youtube/v3")
public interface YoutubeFeignClient {
    @GetMapping("/videos")
    Map<String, Object> getYoutubeInfo(@RequestParam String part, @RequestParam String id, @RequestParam String key);
}
