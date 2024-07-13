package com.aloa.common.video.youtube;

import com.aloa.common.util.SyncProcessor;
import com.aloa.common.video.entity.Video;
import com.aloa.common.video.finder.VideoFinder;
import com.aloa.common.video.manager.VideoSaveManager;
import com.aloa.common.video.validator.VideoValidator;
import com.aloa.online.video.calculator.VideoCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Component
@RequiredArgsConstructor
public class YoutubeDownloader {
    private final VideoValidator videoValidator;
    private final VideoSaveManager videoSaveManager;
    private final VideoCalculator videoCalculator;
    private final VideoFinder videoFinder;
    private final SyncProcessor syncProcessor;
    private final Executor calculationExecutor;
    private static final String downloadPath = "/src/main/resources/ffmpeg/bin/video/";
    private static final String videoFormat = ".mkv";

    public void download(Video video){
        //재조회
        video = videoFinder.findByPath(video.getPath()).orElse(video);

//        videoSaveManager.notifyDownloading(video);

        /** 구글에서 유튜브 비디오 다운로드 */
        final String outputFilePath;
        try {
            outputFilePath = downloadVideo(video.getPath());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }


//        requestCalculation(video, outputFilePath);
    }

    private String downloadVideo(String url) throws IOException, InterruptedException {
        String outputFilePath = videoValidator.extractVideoId(url);

        outputFilePath = downloadPath + outputFilePath;

        ProcessBuilder pb = new ProcessBuilder("yt-dlp", "-o", outputFilePath, url);

        syncProcessor.startProcess(pb, "yt-dlp");
        return outputFilePath;
    }

    private void requestCalculation(Video video, String outputFilePath){
        CompletableFuture.runAsync(() -> videoCalculator.calculate(video, outputFilePath), calculationExecutor);
    }


}
