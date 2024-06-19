package com.aloa.common.video.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CalculationState {
    WAITING("계산대기중입니다."),
    CALCULATING("계산중입니다."),
    COMPLETED("계산완료되었습니다.");
    
    private final String message;
}
