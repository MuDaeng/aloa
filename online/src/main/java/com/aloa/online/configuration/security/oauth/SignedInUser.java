package com.aloa.online.configuration.security.oauth;

import lombok.AccessLevel;
import lombok.Builder;

import java.util.Optional;

@Builder(access = AccessLevel.PACKAGE)
public class SignedInUser {
    private Optional<OAuth2UserInfo> user;

    public boolean isSignedIn(){
        return user.isPresent();
    }

    public long getUserId(){
        return user.map(OAuth2UserInfo::email)
                .map(Long::valueOf)
                .orElse(0L);
    }

    public String getName(){
        return user.map(OAuth2UserInfo::name).orElse(null);
    }
}
