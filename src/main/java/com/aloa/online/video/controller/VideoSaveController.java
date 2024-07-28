package com.aloa.online.video.controller;

import com.aloa.common.StatusResponseDTO;
import com.aloa.online.video.dto.VideoRegisterDTO;
import com.aloa.online.video.service.VideoSaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/video/v1")
public class VideoSaveController {
    private final VideoSaveService videoSaveService;

    @PostMapping("/video")
    public StatusResponseDTO registVideo(@RequestBody(required = false) VideoRegisterDTO videoRegisterDTO){
        videoSaveService.registVideo(videoRegisterDTO);
        return StatusResponseDTO.success();
    }
}
