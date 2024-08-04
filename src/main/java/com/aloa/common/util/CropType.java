package com.aloa.common.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum CropType {
    UHD(21, 7, 1015, ",scale=54:54", "_card_image"),
    FHD(15, 8, 993, null, "_card_image");

    private final int widthMultiple;
    private final int heightMultiple;
    private final int startYRatio;
    private final String scale;
    private final String annexationName;
}
