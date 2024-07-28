package com.aloa.online.video.controller;

import com.aloa.online.video.dto.VideoInquiryDTO;
import com.aloa.online.video.service.VideoInquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/video/inquiry")
public class VideoInquiryController {
    private final VideoInquiryService videoInquiryService;

    @GetMapping("/v1/id")
    public VideoInquiryDTO findByVideoId(@RequestParam Long videoId) {
        return videoInquiryService.findByVideoId(videoId);
    }
}
