package com.aloa.online.video.event;

import com.aloa.common.video.handler.VideoFinder;
import com.aloa.common.video.handler.YoutubeDownloader;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class VideoRegEventListener {
    private final YoutubeDownloader youtubeDownloader;
    private final VideoFinder videoFinder;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Async("downloadExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void downloadYoutubeVideo(VideoRegEvent event) {
        var video = videoFinder.findById(event.videoId())
                .orElseThrow(() -> new IllegalArgumentException("비디오가 미존재"));

        youtubeDownloader.download(video);

        applicationEventPublisher.publishEvent(new DownloadCompletedEvent(video.getId()));
    }
}
