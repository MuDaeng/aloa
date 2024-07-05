package com.aloa.common.util;

import com.aloa.common.video.entity.Video;
import com.aloa.common.security.oauth.OAuth2UserInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;

import java.util.Optional;

@Builder(access = AccessLevel.PACKAGE)
public class SignedInUser {
    private Optional<OAuth2UserInfo> user;

    public boolean isSignedIn(){
        return user.isPresent();
    }

    public String getUserId(){
        return user.map(OAuth2UserInfo::email).orElse(null);
    }

    public String getName(){
        return user.map(OAuth2UserInfo::name).orElse(null);
    }

    public boolean isVideoOfUser(@NonNull Video video){
        if(!isSignedIn()) return false;

        if(video.getPath() == null) throw new IllegalArgumentException("video path is null");

        String googleEmail = user.map(OAuth2UserInfo::email).orElse("");

        if(googleEmail.isBlank()) return false;

        return googleEmail.equals(video.getPath());
    }
}
