package com.aloa.common.video.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CalculationState {
    WAITING("계산대기중입니다."),
    DOWNLOADING("영상을 다운받고 있습니다."),
    CALCULATING("계산중입니다."),
    COMPLETED("계산완료되었습니다."),
    FAILED_DOWNLOAD("다운로드실패");
    
    private final String message;
}
