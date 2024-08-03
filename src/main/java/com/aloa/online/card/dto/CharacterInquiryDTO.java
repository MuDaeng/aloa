package com.aloa.online.card.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CharacterInquiryDTO {
    private String characterName;
    private List<CardCntDTO> cardList;
}
