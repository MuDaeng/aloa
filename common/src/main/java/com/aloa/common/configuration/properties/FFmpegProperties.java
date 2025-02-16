package com.aloa.common.configuration.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "ffmpeg")
@RequiredArgsConstructor
public class FFmpegProperties {
    private final String ffmpegPath;
    private final String ffprobePath;
}
