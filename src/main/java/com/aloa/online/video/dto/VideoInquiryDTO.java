package com.aloa.online.video.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class VideoInquiryDTO {
    private String path;
    private String characterName;
    private List<CardCntDTO> cardList;
    private List<CardCntDTO> independentTrials;
}
