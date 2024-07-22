package com.aloa.common.video.handler;

import com.aloa.common.card.entity.Card;

public record CardLifePeriod(Card card, int start, int end) {
}
