package com.aloa.common.video.handler;

import com.aloa.common.video.entity.Video;
import lombok.RequiredArgsConstructor;

import java.io.Closeable;

@RequiredArgsConstructor
public class PostCalculator implements Closeable {
    private final VideoCalculator videoCalculator;
    private final Video video;

    @Override
    public void close() {
        videoCalculator.postProcessCalculation(video);
    }
}
