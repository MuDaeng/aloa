package com.aloa.common.video.youtube;

import com.aloa.common.video.validator.VideoValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
@RequiredArgsConstructor
public class YoutubeDownloader {
    private final VideoValidator videoValidator;

    public String downloadVideo(String path) throws IOException, InterruptedException {
        String outputFilePath = videoValidator.extractVideoId(path);

        ProcessBuilder pb = new ProcessBuilder("yt-dlp", "-o", outputFilePath, path);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("yt-dlp failed with exit code " + exitCode);
        }
        return outputFilePath;
    }
}
