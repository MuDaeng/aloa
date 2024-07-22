package com.aloa.common.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum CropType {
    NAME(15, 15, 1000, ",scale=384:216", ""),
    IMAGE(15, 8, 993, null, "_card_image");

    private final int widthMultiple;
    private final int heightMultiple;
    private final int startYRatio;
    private final String scale;
    private final String annexationName;
}
