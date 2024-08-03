package com.aloa.online.card.controller;

import com.aloa.online.card.dto.CharacterInquiryDTO;
import com.aloa.online.card.dto.VideoInquiryDTO;
import com.aloa.online.card.service.CardInquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/card/inquiry")
public class CardInquiryController {
    private final CardInquiryService cardInquiryService;

    @GetMapping("/v1/id")
    public VideoInquiryDTO findByVideoId(@RequestParam Long videoId) {
        return cardInquiryService.findByVideoId(videoId);
    }

    @GetMapping("/v1/character")
    public CharacterInquiryDTO findByCharacterId(@RequestParam Long expeditionId,
                                                 @RequestParam Integer characterId) {
        return cardInquiryService.findByCharacterId(expeditionId, characterId);
    }
}
