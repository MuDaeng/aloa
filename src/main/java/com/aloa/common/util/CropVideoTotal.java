package com.aloa.common.util;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PACKAGE)
public record CropVideoTotal(String zName, String xName, String zImage, String xImage) {
}
