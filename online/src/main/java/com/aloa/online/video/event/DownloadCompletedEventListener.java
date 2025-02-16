package com.aloa.online.video.event;

import com.aloa.common.video.handler.PostCalculator;
import com.aloa.common.video.handler.VideoCalculator;
import com.aloa.common.video.handler.VideoFinder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
@Transactional
public class DownloadCompletedEventListener {
    private final VideoFinder videoFinder;
    private final VideoCalculator videoCalculator;

    @EventListener
    public void calculateVideo(DownloadCompletedEvent event) throws IOException {
        var video = videoFinder.findById(event.videoId())
                .orElseThrow(() -> new IllegalArgumentException("비디오가 미존재"));

        try(var postCalculator = new PostCalculator(videoCalculator, video)) {
            videoCalculator.calculate(video);
        }
    }
}
