package com.aloa.online.card.dto;

import com.aloa.common.card.dto.CardCnt;
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
    private String engrave;
    private String sideNode;
    private List<CardCnt> cardList;
    private List<CardCnt> independentTrials;
}
