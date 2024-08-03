package com.aloa.online.card.dto;

import com.aloa.common.card.entity.Card;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CardCntDTO {
    private Card card;
    private Long cnt;
}
