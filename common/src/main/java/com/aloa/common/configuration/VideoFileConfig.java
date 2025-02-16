package com.aloa.common.configuration;

import com.aloa.common.configuration.properties.FFmpegProperties;
import lombok.RequiredArgsConstructor;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class VideoFileConfig {
    private final FFmpegProperties fFmpegProperties;
    @Bean
    public FFmpeg fFmpeg(){
        try {
            return new FFmpeg(fFmpegProperties.getFfmpegPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Bean
    public FFprobe fFprobe(){
        try {
            return new FFprobe(fFmpegProperties.getFfprobePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public Tesseract tesseract(){
        Tesseract tesseract = new Tesseract();
        tesseract.setLanguage("kor");
        tesseract.setOcrEngineMode(1);
        tesseract.setPageSegMode(6);
        return tesseract;
    }
}
