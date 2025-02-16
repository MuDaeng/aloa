package com.aloa.online.card.controller;

import com.aloa.online.card.dto.CardConditionSumInquiryDTO;
import com.aloa.online.card.dto.CharacterInquiryDTO;
import com.aloa.online.card.dto.VideoInquiryDTO;
import com.aloa.online.card.service.UserCardInquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/card/inquiry")
public class UserCardInquiryController {
    private final UserCardInquiryService userCardInquiryService;

    @GetMapping("/v1/id")
    public VideoInquiryDTO findByVideoId(@RequestParam Long videoId) {
        return userCardInquiryService.findByVideoId(videoId);
    }

    @GetMapping("/v1/character")
    public CharacterInquiryDTO findByCharacterId(@RequestParam Long expeditionId,
                                                 @RequestParam Integer characterId) {
        return userCardInquiryService.findByCharacterId(expeditionId, characterId);
    }

    @GetMapping("/v1/user")
    public List<CardConditionSumInquiryDTO> findByUserId(@RequestParam Long userId) {
        return userCardInquiryService.findByUserId(userId);
    }
}
