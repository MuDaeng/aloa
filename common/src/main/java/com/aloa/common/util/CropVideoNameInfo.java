package com.aloa.common.util;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PACKAGE)
public record CropVideoNameInfo(String z, String x) {
}
