package com.aloa.online.video.event;

import com.aloa.common.video.handler.VideoCalculator;
import com.aloa.common.video.handler.VideoFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DownloadCompletedEventListener {
    private final VideoFinder videoFinder;
    private final VideoCalculator videoCalculator;

    @EventListener
    public void calculateVideo(DownloadCompletedEvent event){
        var video = videoFinder.findById(event.videoId())
                .orElseThrow(() -> new IllegalArgumentException("비디오가 미존재"));

        videoCalculator.calculate(video);
    }
}
