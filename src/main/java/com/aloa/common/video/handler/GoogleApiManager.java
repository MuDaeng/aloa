package com.aloa.common.video.handler;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoSnippet;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GoogleApiManager {
    /**
     * apiKey
     */
    @Value("${youtube.api.key}")
    private String apiKey;

    private final VideoValidator videoValidator;

    public YoutubeInfo getYoutubeInfo(@NonNull String path) {
        var youtubeVideoId = videoValidator.extractVideoId(path);

        var snippet = getYoutubeSnippet(youtubeVideoId);

        return YoutubeInfo.builder()
                .title(snippet.getTitle())
                .description(snippet.getDescription())
                .path(path)
                .youtubeVideoId(youtubeVideoId)
                .publishedAt(snippet.getPublishedAt())
                .channelId(snippet.getChannelId())
                .build();
    }

    private VideoSnippet getYoutubeSnippet(@NonNull String videoId) {
        Optional<VideoListResponse> response;
        var youtube = createYoutubeRequest();
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
                .filter(list -> !list.isEmpty())
                .map(List::getFirst)
                .map(com.google.api.services.youtube.model.Video::getSnippet)
                .orElse(null);
    }

    public String getChannelId(String oAuth2AccessToken){
        var youtube = createYoutubeRequest();
        try{
            var response = youtube.channels().list(List.of("id"))
                    .setAccessToken(oAuth2AccessToken)
                    .setKey(apiKey)
                    .setMine(true)
                    .execute();
            return Optional.ofNullable(response)
                    .stream()
                    .map(ChannelListResponse::getItems)
                    .flatMap(Collection::stream)
                    .map(Channel::getId)
                    .findFirst()
                    .orElse(null);
        }catch(IOException e){
            throw new IllegalArgumentException(e);
        }
    }

    private YouTube createYoutubeRequest(){
        return new YouTube.Builder(
                new NetHttpTransport(),
                // JSON 데이터를 처리하기 위한 JsonFactory 객체 생성
                GsonFactory.getDefaultInstance(),
                httpRequest -> {}
        ).build();
    }
}