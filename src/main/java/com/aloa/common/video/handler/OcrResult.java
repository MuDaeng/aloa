package com.aloa.common.video.handler;

import lombok.NonNull;

import java.util.List;

public record OcrResult(@NonNull List<String> zList, @NonNull List<String> xList) {
}
