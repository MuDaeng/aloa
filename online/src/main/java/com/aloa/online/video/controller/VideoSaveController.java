package com.aloa.online.video.controller;

import com.aloa.common.StatusResponseDTO;
import com.aloa.online.video.dto.VideoMappingDTO;
import com.aloa.online.video.dto.VideoRegisterDTO;
import com.aloa.online.video.service.VideoSaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/video")
public class VideoSaveController {
    private final VideoSaveService videoSaveService;

    @PostMapping("/v1")
    public StatusResponseDTO registerVideo(@RequestBody VideoRegisterDTO videoRegisterDTO){
        return StatusResponseDTO.success(videoSaveService.registerVideo(videoRegisterDTO));
    }

    @PutMapping("/v1/character")
    public StatusResponseDTO changeCharacter(@RequestBody @Valid VideoMappingDTO videoMappingDTO){
        videoSaveService.changeCharacter(videoMappingDTO);
        return StatusResponseDTO.success();
    }
}
