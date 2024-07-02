package com.aloa.common.util;

import com.aloa.common.video.entity.Video;
import com.aloa.configuration.oauth.OAuth2UserInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;

import java.util.Optional;

@Builder(access = AccessLevel.NONE)
public class SignedInUser {
    private Optional<OAuth2UserInfo> loginUser;

    public boolean isSignedIn(){
        return loginUser.isPresent();
    }

    public String getUserId(){
        return loginUser.map(OAuth2UserInfo::email).orElse(null);
    }

    public String getName(){
        return loginUser.map(OAuth2UserInfo::name).orElse(null);
    }

    public boolean isVideoOfUser(@NonNull Video video){
        if(!isSignedIn()) return false;

        if(video.getPath() == null) throw new IllegalArgumentException("video path is null");

        String googleEmail = loginUser.map(OAuth2UserInfo::email).orElse("");

        if(googleEmail.isBlank()) return false;

        return googleEmail.equals(video.getPath());
    }
}
