package com.aloa.common.video.handler;

import com.aloa.common.util.SyncProcessor;
import com.aloa.common.video.entity.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class YoutubeDownloader {
    private final VideoValidator videoValidator;
    private final SyncProcessor syncProcessor;
    private static final String downloadPath = "/src/main/resources/ffmpeg/bin/video/";

    public void download(Video video){
        /** 구글에서 유튜브 비디오 다운로드 */
        try {
            String outputFilePath = videoValidator.extractVideoId(video.getPath());
            outputFilePath = downloadPath + outputFilePath;

            ProcessBuilder pb = new ProcessBuilder("yt-dlp", "-o", outputFilePath, video.getPath());
            syncProcessor.startProcess(pb, "yt-dlp");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
