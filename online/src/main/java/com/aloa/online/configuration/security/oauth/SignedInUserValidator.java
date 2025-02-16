package com.aloa.online.configuration.security.oauth;

import com.aloa.common.user.repository.GoogleMappingRepository;
import com.aloa.common.video.handler.VideoFinder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SignedInUserValidator {
    private final SignedInUserFinder signedInUserFinder;
    private final GoogleMappingRepository googleMappingRepository;
    private final VideoFinder videoFinder;


    public boolean isUserOfVideo(@NonNull String channelId){
        var signedInUser = SignedInUserFinder.getSignedInUser();

        if(!signedInUser.isSignedIn()) return false;

        Optional.of(channelId)
                .filter(str -> !str.isBlank())
                .orElseThrow(() -> new IllegalArgumentException("video path is null"));

        var googleUserId = signedInUserFinder.findSignedInUserId();

        return googleMappingRepository.findByGoogleUserId(googleUserId)
                .filter(googleMapping -> channelId.equals(googleMapping.getChannelId()))
                .isPresent();
    }

    public boolean isUserOfVideo(@NonNull Long videoId) {
        var signedInUser = SignedInUserFinder.getSignedInUser();

        if (!signedInUser.isSignedIn()) return false;

        var googleUserId = signedInUserFinder.findSignedInUserId();

        return videoFinder.findMappingByVideoId(videoId)
                .filter(videoMapping -> googleUserId.equals(videoMapping.getGoogleUserId()))
                .isPresent();
    }
}
