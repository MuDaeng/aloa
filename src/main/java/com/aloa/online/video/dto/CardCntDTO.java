package com.aloa.online.video.dto;

import com.aloa.common.card.entity.Card;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardCntDTO {
    private Card card;
    private Long cnt;
}
