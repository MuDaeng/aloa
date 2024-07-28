package com.aloa.online.video.event;

import com.aloa.common.video.entity.Video;
import com.aloa.common.video.handler.VideoCalculator;
import com.aloa.common.video.handler.VideoFinder;
import com.aloa.common.video.handler.YoutubeDownloader;
import com.aloa.common.video.manager.VideoSaveManager;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Component
@RequiredArgsConstructor
public class VideoRegEventListener {
    private final YoutubeDownloader youtubeDownloader;
    private final VideoFinder videoFinder;
    private final VideoSaveManager videoSaveManager;
    private final VideoCalculator videoCalculator;
    private final Executor calculationExecutor;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void videoRegEvent(VideoRegEvent event) {
        var videoOptional = videoFinder.findById(event.getVideoId());

        var video = videoOptional.orElseThrow(() -> new IllegalArgumentException("비디오가 미존재"));

        videoSaveManager.notifyDownloading(video);

        youtubeDownloader.download(video);

        requestCalculation(video);
    }

    private void requestCalculation(Video video){
        CompletableFuture.runAsync(() -> videoCalculator.calculate(video), calculationExecutor);
    }
}
