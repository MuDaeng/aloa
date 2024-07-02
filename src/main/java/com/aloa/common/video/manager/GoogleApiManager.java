package com.aloa.common.video.manager;

import com.aloa.common.client.entity.ClientVersion;
import com.aloa.common.client.manager.ClientVersionManager;
import com.aloa.common.util.ChosungExtractor;
import com.aloa.common.video.entity.Video;
import com.aloa.common.video.validator.VideoValidator;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoSnippet;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GoogleApiManager {
    /** apiKey */
    @Value("${youtube.api.key}")
    private String apiKey;

    private final VideoValidator videoValidator;
    private final ClientVersionManager clientVersionManager;
    private final YouTube youtube;

    public Video getYoutubeInfo(@NonNull String path){
        var youtubeVideoId = videoValidator.extractVideoId(path);

        VideoSnippet snippet = getYoutubeSnippet(youtubeVideoId);

        if(snippet == null){
            return null;
        }

        return Video.builder()
                .title(snippet.getTitle())
                .description(snippet.getDescription())
                .path(path)
                .youtubeVideoId(youtubeVideoId)
                .chosung(ChosungExtractor.extractChosung(snippet.getTitle()))
                .clientVersion(
                        Optional.ofNullable(
                                clientVersionManager.getClientVersion(snippet.getPublishedAt())
                                )
                                .map(ClientVersion::getVersion)
                                .orElse(null))
                .build();
    }

    public VideoSnippet getYoutubeSnippet(@NonNull String videoId){
        Optional<VideoListResponse> response;

        try {
            response = Optional.ofNullable(
                    youtube.videos()
                            .list(List.of("id", "snippet", "contentDetails"))
                            .setKey(apiKey)
                            .setId(List.of(videoId))
                            .execute()
            );
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }

        return response.map(VideoListResponse::getItems)
                .map(List::getFirst)
                .map(com.google.api.services.youtube.model.Video::getSnippet)
                .orElse(null);
    }
}
