package com.aloa.configuration;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class YoutubeConfig {
    @Bean
    public YouTube youtube(){

        YouTube youtube = new YouTube.Builder(
                new NetHttpTransport(),
        // JSON 데이터를 처리하기 위한 JsonFactory 객체 생성
                GsonFactory.getDefaultInstance(),
                httpRequest -> {}
        ).build();
        return youtube;
    }
}
