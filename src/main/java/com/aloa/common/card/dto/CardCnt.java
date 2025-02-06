package com.aloa.common.card.dto;

import com.aloa.common.card.entity.Card;
import lombok.*;

@Builder
public record CardCnt(Card card, long cnt) {
}
