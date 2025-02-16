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
public class CardConditionSumInquiryDTO {
    private String engrave;
    private String sideNode;
    private boolean isIndependentTrials;
    private List<CardCnt> cardCntList;
}

